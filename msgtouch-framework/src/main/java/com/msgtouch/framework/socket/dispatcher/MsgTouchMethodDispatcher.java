package com.msgtouch.framework.socket.dispatcher;

import com.msgtouch.framework.exception.MsgTouchException;
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
public class MsgTouchMethodDispatcher {
    private static Logger logger= LoggerFactory.getLogger(MsgTouchMethodDispatcher.class);

    private Map<String,MsgTouchMethodInvoker> methodInvokerMap =new HashMap<String,MsgTouchMethodInvoker>();
    private Map<String,List<MsgPushedListener>> pushedListenerMap=new HashMap<String,List<MsgPushedListener>>();

    private List<String> clusterList=new ArrayList<String>();
    private boolean handlerPush;
    public MsgTouchMethodDispatcher(){
        this.handlerPush=false;
    }

    public MsgTouchMethodDispatcher(boolean handlerPush){
        this.handlerPush=handlerPush;
    }

    public void addMethod(String cmd,MsgTouchMethodInvoker invoker){
        if(methodInvokerMap.containsKey(cmd)){
            logger.error("RpcService method has exists:cmd={}",cmd);
            throw new MsgTouchException("RpcService method "+cmd+"has exists");
        }
        methodInvokerMap.put(cmd, invoker);
    }

    public void dispatcher(ISession session, MsgPacket msgPacket){
        if(msgPacket.isCall()){
            String cmd=msgPacket.getCmd();
            MsgTouchMethodInvoker msgTouchMethodInvoker=methodInvokerMap.get(cmd);
            if(null==msgTouchMethodInvoker){
                if(handlerPush){
                    handlerPush(session,msgPacket);
                    return;
                }else{
                    throw new RuntimeException("MsgTouchMethodDispatcher method cmd="+cmd+" not found!");
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
                    logger.info("MsgTouchMethodDispatcher invoke method exception ！！");
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

    public void addCluster(String cluster){
        if(!clusterList.contains(cluster)){
            clusterList.add(cluster);
        }
    }

    public List<String> getClusterlist() {
        return clusterList;
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

    private String getParameterizedTypeName(MsgPushedListener msgPushedListener){
        String result=null;
        Type[] types = msgPushedListener.getClass().getGenericInterfaces();
        for(Type t:types){
            if(t instanceof ParameterizedType) {
                Type[] temp = ((ParameterizedType) t).getActualTypeArguments();
                for(Type type:temp){
                    result=((Class)type).getName();
                }
            }
        }
        return result;
    }

}
