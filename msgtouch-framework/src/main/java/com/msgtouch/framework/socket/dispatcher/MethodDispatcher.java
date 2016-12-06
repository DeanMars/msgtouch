package com.msgtouch.framework.socket.dispatcher;

import com.msgtouch.framework.exception.MsgTouchException;
import com.msgtouch.framework.socket.session.ISession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private List<String> clusterList=new ArrayList<String>();
    protected void initPool(int threadSize){
        if(null==pool){
            pool=Executors.newFixedThreadPool(threadSize, new ThreadFactory() {
                private AtomicInteger size = new AtomicInteger();
                @Override
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
            throw new MsgTouchException("RpcService method "+cmd+"has exists");
        }
        methodInvokerMap.put(cmd, invoker);
    }


    public abstract void dispatcher(ISession session, T t);


    protected String getParameterizedTypeName(MsgPushedListener msgPushedListener){
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

    public void addCluster(String cluster){
        if(!clusterList.contains(cluster)){
            clusterList.add(cluster);
        }
    }

    public List<String> getClusterlist() {
        return clusterList;
    }
}
