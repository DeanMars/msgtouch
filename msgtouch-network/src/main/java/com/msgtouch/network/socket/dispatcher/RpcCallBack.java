package com.msgtouch.network.socket.dispatcher;

import com.msgtouch.network.socket.session.ISession;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Dean on 2017/1/6.
 */
public interface RpcCallBack<T> {
    void processResult(ISession session,T result);

    void processException(Throwable throwable);

    T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException;

}
