package com.msgtouch.network.socket.dispatcher;

import com.msgtouch.network.annotation.Unauthorization;
import com.msgtouch.network.context.Constraint;
import com.msgtouch.network.exception.MsgTouchException;
import com.msgtouch.network.socket.listener.MsgPushedListener;
import com.msgtouch.network.socket.packet.MsgPBPacket;
import com.msgtouch.network.socket.session.ISession;
import com.msgtouch.network.socket.session.Session;
import com.msgtouch.network.socket.session.SessionManager;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dean on 2016/9/8.
 */
public class PBPacketMethodDispatcher extends MethodDispatcher<MsgPBPacket.Packet.Builder>{
    private static Logger logger= LoggerFactory.getLogger(PBPacketMethodDispatcher.class);

    public PBPacketMethodDispatcher(int threadSize){
        initPool(threadSize);
    }

    public PBPacketMethodDispatcher(int threadSize,boolean handlerPush){
        this.handlerPush=handlerPush;
        initPool(threadSize);
    }

    public void dispatcher(final ISession session, final MsgPBPacket.Packet.Builder builder){
        if(Constraint.MsgTouchHeartBeats.equals(builder.getCmd())){
            if (builder.getMsgType() == MsgPBPacket.MsgType.Request) {
                responseHeartBeats(session, builder);
            }else{
                handlerResponse(session, builder);
            }
        }else {
            pool.submit(new Runnable() {
                public void run() {
                    //push消息
                    if (builder.getRetCode() == MsgPBPacket.RetCode.PUSH) {
                        handlerPush(session, builder);
                    } else {
                        if (builder.getMsgType() == MsgPBPacket.MsgType.Request) {
                            handlerRequest(session, builder);
                        } else {
                            handlerResponse(session, builder);
                        }
                    }
                }
            });
        }
    }

    private void handlerResponse(ISession session,  MsgPBPacket.Packet.Builder builder){
        RpcCallBack callBack = session.getAttribute(Session.SYNC_CALLBACK_MAP).get(builder.getSeq());
        if (null == callBack) {
            callBack=session.getAttribute(Session.ASYNC_CALLBACK_MAP).get(builder.getSeq());
        }
        if (callBack != null) {
            callBack.processResult(session, builder);
        }
    }

    private void handlerRequest(ISession session,  MsgPBPacket.Packet.Builder builder){
        String cmd = builder.getCmd();
        MsgTouchMethodInvoker msgTouchMethodInvoker = methodInvokerMap.get(cmd);
        if (null == msgTouchMethodInvoker) {
            throw new RuntimeException("PBPacketMethodDispatcher method cmd=" + cmd + " not found!");
        }
        Method method=msgTouchMethodInvoker.getMethod();
        MsgPBPacket.Packet.Builder ret=builder;
        Unauthorization unauthorization=method.getAnnotation(Unauthorization.class);
        if(unauthorization==null&&!authorization(ret)){
            logger.error("PBPacketMethodDispatcher Unauthorization ！！packet={}",ret.toString());
            ret.setRetCode(MsgPBPacket.RetCode.ERROR_NO_SESSION);
            responseClient(session,ret);
            return;
        }
        Class []types=method.getParameterTypes();
        List<Object> list=new ArrayList<Object>();
        for(Class clazz:types){
            if((clazz.isInterface()&&clazz.getName().equals(ISession.class.getName()))
                    ||(!clazz.isInterface()&&clazz.getName().equals(Session.class.getName()))){
                list.add(session);
            }else{
                list.add(builder);
            }
        }
        Object[] params = list.toArray();
        try {
            ret = (MsgPBPacket.Packet.Builder) msgTouchMethodInvoker.invoke(params);
        } catch (Exception e) {
            logger.error("PBPacketMethodDispatcher invoke method exception ！！");
            e.printStackTrace();
            ret.setError(e.getMessage());
            ret.setRetCode(MsgPBPacket.RetCode.EXCEPTION);
        }
        responseClient(session,ret);
    }



    private void responseHeartBeats(ISession session,  MsgPBPacket.Packet.Builder builder) {
        builder.setRetCode(MsgPBPacket.RetCode.OK);
        long now=System.currentTimeMillis();
        session.setLastActiveTime(now);
        logger.info("PBPacketMethodDispatcher responseHeartBeats channel={},time={} ms,uid={},gameId={}",session.getChannel().toString(),now,builder.getUid(),builder.getGameId());
        responseClient(session,builder);
    }


    private void responseClient(ISession session,MsgPBPacket.Packet.Builder ret){
        ret.setMsgType(MsgPBPacket.MsgType.Response);
        Channel channel = session.getChannel();
        if (channel.isActive()) {
            channel.writeAndFlush(ret);
        } else {
            logger.error("channel is not active:packet = {}", ret.build().toString());
        }
    }


    private boolean authorization(MsgPBPacket.Packet.Builder packet){
        boolean ret=false;
        long uid=packet.getUid();
        String gameId=packet.getGameId();
        ISession session= SessionManager.getInstance().getSession(uid+"_"+gameId);
        if(null!=session&&session.isActive()){
            return true;
        }
        return ret;
    }



    private void handlerPush(ISession session, MsgPBPacket.Packet.Builder builder){
        if(null!=builder){
            List<MsgPushedListener> list=pushedListenerMap.get(MsgPBPacket.Packet.Builder.class.getName());
            MsgPBPacket.Packet.Builder ret=null;
            if(null!=list){
                for(MsgPushedListener msgPushedListener:list){
                    ret=(MsgPBPacket.Packet.Builder )msgPushedListener.msgReceived(builder);
                }
            }else{
                ret=builder;
            }
            ret.setMsgType( MsgPBPacket.MsgType.Response);
            Channel channel=session.getChannel();
            if(channel.isActive()) {
                channel.writeAndFlush(ret);
            }else{
                logger.error("channel is not active:packet = {}",builder.toString());
            }
        }

    }


}
