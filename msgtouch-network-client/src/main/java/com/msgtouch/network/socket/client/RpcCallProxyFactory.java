package com.msgtouch.network.socket.client;

/**
 * Created by Dean on 2016/11/23.
 */
public interface RpcCallProxyFactory {
    class CallProxyEntry<T>{
        T syncProxy;
        T asyncProxy;
    }
    <T> T getRpcCallProxy(boolean sync,Class<T> clazz);
    <T> CallProxyEntry<T> createCallProxyEntry(Class<T> clazz);
}
