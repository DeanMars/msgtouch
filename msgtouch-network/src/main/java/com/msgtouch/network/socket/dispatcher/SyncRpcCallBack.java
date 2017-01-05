package com.msgtouch.network.socket.dispatcher;

import com.msgtouch.network.socket.session.ISession;
import io.netty.util.concurrent.DefaultProgressivePromise;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class SyncRpcCallBack<T> {
    private DefaultProgressivePromise<T> progressPromise;
    public SyncRpcCallBack(DefaultProgressivePromise<T> progressPromise) {
        this.progressPromise = progressPromise;
    }

    public void processResult(ISession session, T result) {
        progressPromise.setSuccess(result);
    }


    public void processException(Throwable throwable) {
        progressPromise.setFailure(throwable);
    }


    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return progressPromise.get(timeout,unit);
    }
}
