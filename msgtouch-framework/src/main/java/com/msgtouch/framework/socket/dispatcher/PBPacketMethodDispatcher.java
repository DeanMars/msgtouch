package com.msgtouch.framework.socket.dispatcher;

import com.msgtouch.common.proto.MsgPBPacket;
import com.msgtouch.framework.exception.MsgTouchException;
import com.msgtouch.framework.socket.session.ISession;
import com.msgtouch.framework.socket.session.Session;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by Dean on 2016/9/8.
 */
public class PBPacketMethodDispatcher extends MethodDispatcher<MsgPBPacket.Packet.Builder>{
    private static Logger logger= LoggerFactory.getLogger(PBPacketMethodDispatcher.class);

    private Map<String,MsgTouchMethodInvoker> methodInvokerMap =new HashMap<String,MsgTouchMethodInvoker>();
    private Map<String,List<MsgPushedListener>> pushedListenerMap=new HashMap<String,List<MsgPushedListener>>();
    private List<String> clusterList=new ArrayList<String>();
    private boolean handlerPush;
    public PBPacketMethodDispatcher(int threadSize){
        initPool(threadSize);
    }
    public PBPacketMethodDispatcher(int threadSize, boolean handlerPush){
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
                if(builder.getMsgType().getNumber()==MsgPBPacket.MsgType.Request.getNumber()){
                    String cmd=builder.getCmd();
                    MsgTouchMethodInvoker msgTouchMethodInvoker=methodInvokerMap.get(cmd);
                    if(null==msgTouchMethodInvoker){
                        throw new RuntimeException("JsonPacketMethodDispatcher method cmd="+cmd+" not found!");
                    }
                    Object[] params = new Object[]{builder};
                    try {
                        MsgPBPacket.Packet.Builder ret=(MsgPBPacket.Packet.Builder)msgTouchMethodInvoker.invoke(params);
                        ret.setMsgType(MsgPBPacket.MsgType.Response);
                        Channel channel=session.getChannel();
                        if(channel.isActive()) {
                            channel.writeAndFlush(ret);
                        }else{
                            logger.error("channel is not active:packet = {}",builder.build().toString());
                        }
                    } catch (Exception e) {
                        logger.info("JsonPacketMethodDispatcher invoke method exception ！！");
                        e.printStackTrace();
                    }
                }else{
                    SyncRpcCallBack callBack=session.getAttribute(Session.SYNC_CALLBACK_MAP).get(builder.getSeq());
                    if(callBack!=null){
                        callBack.processResult(session,builder);
                    }
                }
            }
        });


    }






}
