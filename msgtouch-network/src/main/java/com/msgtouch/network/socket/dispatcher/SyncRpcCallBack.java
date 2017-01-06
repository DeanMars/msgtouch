package com.msgtouch.network.socket.dispatcher;

import com.msgtouch.network.socket.session.ISession;
import io.netty.util.concurrent.DefaultProgressivePromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class SyncRpcCallBack<T> implements RpcCallBack<T>{
    private static Logger logger= LoggerFactory.getLogger(SyncRpcCallBack.class);
    private DefaultProgressivePromise<T> progressPromise;
    public SyncRpcCallBack(DefaultProgressivePromise<T> progressPromise) {
        this.progressPromise = progressPromise;
    }

    public void processResult(ISession session, T result) {
        progressPromise.setSuccess(result);
    }


    public void processException(Throwable throwable) {
        progressPromise.setFailure(throwable);
        logger.error("SyncRpcCallBack processException throwable={}",throwable.getMessage());
    }


    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return progressPromise.get(timeout,unit);
    }
}
