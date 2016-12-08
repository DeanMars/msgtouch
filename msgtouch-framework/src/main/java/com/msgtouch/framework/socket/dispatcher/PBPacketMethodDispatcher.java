package com.msgtouch.framework.socket.dispatcher;

import com.msgtouch.framework.exception.MsgTouchException;
import com.msgtouch.framework.socket.client.AbstractPBMsgPushedListener;
import com.msgtouch.framework.socket.client.MsgPushedListener;
import com.msgtouch.framework.socket.packet.MsgPBPacket;
import com.msgtouch.framework.socket.packet.MsgPacket;
import com.msgtouch.framework.socket.session.ISession;
import com.msgtouch.framework.socket.session.Session;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

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


    public void addMethod(String cmd,MsgTouchMethodInvoker invoker){
        if(methodInvokerMap.containsKey(cmd)){
            logger.error("RpcService method has exists:cmd={}",cmd);
            throw new MsgTouchException("RpcService method "+cmd+"has exists");
        }
        methodInvokerMap.put(cmd, invoker);
    }

    public void dispatcher(final ISession session, final MsgPBPacket.Packet.Builder builder){
        pool.submit(new Runnable() {
            @Override
            public void run() {
                //push消息
                if(builder.getRetCode()==MsgPBPacket.RetCode.PUSH){
                    handlerPush(session,builder);
                }else {
                    if (builder.getMsgType() == MsgPBPacket.MsgType.Request) {
                        String cmd = builder.getCmd();
                        MsgTouchMethodInvoker msgTouchMethodInvoker = methodInvokerMap.get(cmd);
                        if (null == msgTouchMethodInvoker) {
                            throw new RuntimeException("PBPacketMethodDispatcher method cmd=" + cmd + " not found!");
                        }
                        Object[] params = new Object[]{builder};
                        try {
                            MsgPBPacket.Packet.Builder ret = (MsgPBPacket.Packet.Builder) msgTouchMethodInvoker.invoke(params);
                            ret.setMsgType(MsgPBPacket.MsgType.Response);
                            Channel channel = session.getChannel();
                            if (channel.isActive()) {
                                channel.writeAndFlush(ret);
                            } else {
                                logger.error("channel is not active:packet = {}", builder.build().toString());
                            }
                        } catch (Exception e) {
                            logger.info("PBPacketMethodDispatcher invoke method exception ！！");
                            e.printStackTrace();
                        }
                    } else {
                        SyncRpcCallBack callBack = session.getAttribute(Session.SYNC_CALLBACK_MAP).get(builder.getSeq());
                        if (callBack != null) {
                            callBack.processResult(session, builder);
                        }
                    }
                }
            }
        });


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
