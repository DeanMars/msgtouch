package com.msgtouch.network.socket.dispatcher;


import com.msgtouch.network.socket.listener.AbstractPBMsgPushedListener;
import com.msgtouch.network.socket.listener.MsgPushedListener;
import com.msgtouch.network.socket.packet.MsgPBPacket;
import com.msgtouch.network.socket.session.ISession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Dean on 2016/9/8.
 */
public abstract class MethodDispatcher<T> {
    private static Logger logger= LoggerFactory.getLogger(MethodDispatcher.class);
    protected Map<String,MsgTouchMethodInvoker> methodInvokerMap =new HashMap<String,MsgTouchMethodInvoker>();
    protected ExecutorService pool=null;
    protected List<String> clusterList=new ArrayList<String>();
    protected Map<String,List<MsgPushedListener>> pushedListenerMap=new HashMap<String,List<MsgPushedListener>>();
    protected boolean handlerPush;

    protected void initPool(int threadSize){
        if(null==pool){
            pool=Executors.newFixedThreadPool(threadSize, new ThreadFactory() {
                private AtomicInteger size = new AtomicInteger();
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setName("Rpc-Dispatcher-" + size.incrementAndGet());
                    if (thread.isDaemon()) {
                        thread.setDaemon(false);
                    }
                    return thread;
                }
            });
        }
    }

    public void addMethod(String cmd,MsgTouchMethodInvoker invoker){
        if(methodInvokerMap.containsKey(cmd)){
            logger.error("RpcService method has exists:cmd={}",cmd);
            throw new RuntimeException("RpcService method "+cmd+"has exists");
        }
        methodInvokerMap.put(cmd, invoker);
    }


    public abstract void dispatcher(ISession session, T t);


    protected String getParameterizedTypeName(MsgPushedListener msgPushedListener){
        String result=null;
        if(msgPushedListener instanceof AbstractPBMsgPushedListener){
            return MsgPBPacket.Packet.Builder.class.getName();
        }
        Type[] types = msgPushedListener.getClass().getGenericInterfaces();
        if(null!=types) {
            for (Type t : types) {
                result = getParameterizedTypeName(t);
            }
        }
        return result;
    }

    private String getParameterizedTypeName(Type t){
        String result=null;
        if(t instanceof ParameterizedType) {
            Type[] temp = ((ParameterizedType) t).getActualTypeArguments();
            for(Type type:temp){
                result=((Class)type).getName();
            }
        }
        return result;
    }


    public void addCluster(String cluster){
        if(!clusterList.contains(cluster)){
            clusterList.add(cluster);
        }
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

    public List<String> getClusterlist() {
        return clusterList;
    }

    public Set<String> getCmds(){
        return methodInvokerMap.keySet();
    }
}
