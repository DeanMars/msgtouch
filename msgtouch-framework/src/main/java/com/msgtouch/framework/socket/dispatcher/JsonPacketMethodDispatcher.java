package com.msgtouch.framework.socket.dispatcher;

import com.msgtouch.framework.socket.packet.MsgPacket;
import com.msgtouch.framework.socket.session.ISession;
import com.msgtouch.framework.socket.session.Session;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by Dean on 2016/9/8.
 */
public class JsonPacketMethodDispatcher extends MethodDispatcher<MsgPacket>{
    private static Logger logger= LoggerFactory.getLogger(JsonPacketMethodDispatcher.class);

    private Map<String,List<MsgPushedListener>> pushedListenerMap=new HashMap<String,List<MsgPushedListener>>();
    private boolean handlerPush;
    public JsonPacketMethodDispatcher(int threadSize){
        this.handlerPush=false;
        initPool(threadSize);
    }
    public JsonPacketMethodDispatcher(int threadSize, boolean handlerPush){
        this.handlerPush=handlerPush;
        initPool(threadSize);
    }

    public void dispatcher(final ISession session, final MsgPacket msgPacket){
        pool.submit(new Runnable() {
            @Override
            public void run() {
                if(msgPacket.isCall()){
                    String cmd=msgPacket.getCmd();
                    MsgTouchMethodInvoker msgTouchMethodInvoker=methodInvokerMap.get(cmd);
                    if(null==msgTouchMethodInvoker){
                        if(handlerPush){
                            handlerPush(session,msgPacket);
                            return;
                        }else{
                            throw new RuntimeException("JsonPacketMethodDispatcher method cmd="+cmd+" not found!");
                        }
                    }
                    Object[] params = msgPacket.getParams();
                    if(null!=params) {
                        try {
                            Object ret=msgTouchMethodInvoker.invoke(params);
                            msgPacket.setParams(new Object[]{ret});
                            msgPacket.setCall(false);
                            Channel channel=session.getChannel();
                            if(channel.isActive()) {
                                channel.writeAndFlush(msgPacket);
                            }else{
                                logger.error("channel is not active:packet = {}",msgPacket);
                            }
                        } catch (Exception e) {
                            logger.info("JsonPacketMethodDispatcher invoke method exception ！！");
                            e.printStackTrace();
                        }

                    }
                }else{
                    SyncRpcCallBack callBack=session.getAttribute(Session.SYNC_CALLBACK_MAP).get(msgPacket.getUuid());
                    if(callBack!=null){
                        if(msgPacket.getParams().length==1){
                            callBack.processResult(session,msgPacket.getParams()[0]);
                        }else{
                            callBack.processResult(session, msgPacket.getParams());
                        }
                    }
                }
            }
        });


    }


    private void handlerPush(ISession session, MsgPacket msgPacket){
        Object[] objs=msgPacket.getParams();
        if(null!=objs){
            Object obj=objs[0];
            String className=obj.getClass().getName();
            List<MsgPushedListener> list=pushedListenerMap.get(className);
            Object ret=null;
            if(null!=list){
                for(MsgPushedListener msgPushedListener:list){
                    ret=msgPushedListener.msgReceived(obj);
                }
            }
            msgPacket.setParams(new Object[]{ret});
            msgPacket.setCall(false);
            Channel channel=session.getChannel();
            if(channel.isActive()) {
                channel.writeAndFlush(msgPacket);
            }else{
                logger.error("channel is not active:packet = {}",msgPacket);
            }
        }

    }


    public Set<String> getCmds(){
        return methodInvokerMap.keySet();
    }


    public void addPushedListener(MsgPushedListener msgPushedListener){
        if(null!=msgPushedListener){
            String name=getParameterizedTypeName(msgPushedListener);
            List<MsgPushedListener> list=pushedListenerMap.get(name);
            if(list==null){
                list=new ArrayList<MsgPushedListener>();
            }
            list.add(msgPushedListener);
            pushedListenerMap.put(name,list);
        }
    }



}
