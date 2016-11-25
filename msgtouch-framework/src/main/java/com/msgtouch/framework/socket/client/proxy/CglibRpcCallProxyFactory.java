package com.msgtouch.framework.socket.client.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.proxy.Enhancer;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Dean on 2016/11/23.
 */
public class CglibRpcCallProxyFactory implements RpcCallProxyFactory{
    private Map<Class,RpcCallProxyFactory.CallProxyEntry> proxyCache=new HashMap<Class,RpcCallProxyFactory.CallProxyEntry>();
    private static final CglibRpcCallBack SYNC_CALL_CGLIB_INTERCEPTOR=new CglibRpcCallBack(true);
    private static final CglibRpcCallBack ASYNC_CALL_CGLIB_INTERCEPTOR=new CglibRpcCallBack(false);
    private static final Logger log= LoggerFactory.getLogger(CglibRpcCallProxyFactory.class);

    public <T> T getRpcCallProxy(boolean sync,Class<T> clazz) {
        CallProxyEntry<T> entry=proxyCache.get(clazz);
        if(entry==null){
            entry=createCallProxyEntry(clazz);
            proxyCache.put(clazz,entry);
        }
        if(sync){
            return entry.syncProxy;
        }else{
            return entry.asyncProxy;
        }
    }
    public <T> CallProxyEntry<T> createCallProxyEntry(Class<T> clazz) {
        Enhancer syncEnhancer = new Enhancer();//通过类Enhancer创建代理对象
        syncEnhancer.setSuperclass(clazz);//传入创建代理对象的类
        syncEnhancer.setCallback(SYNC_CALL_CGLIB_INTERCEPTOR);//设置回调
        T syncProxy=(T)syncEnhancer.create();//创建代理对象
        Enhancer asyncEnhancer=new Enhancer();
        asyncEnhancer.setSuperclass(clazz);
        asyncEnhancer.setCallback(ASYNC_CALL_CGLIB_INTERCEPTOR);
        T asyncProxy=(T)asyncEnhancer.create();
        CallProxyEntry<T> entry=new CallProxyEntry<T>();
        entry.syncProxy=syncProxy;
        entry.asyncProxy=asyncProxy;
        log.info("Create RpcCallProxy instance :{}", syncProxy.getClass().getName());
        return entry;
    }
}
