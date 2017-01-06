package com.msgtouch.network.socket.dispatcher;

import com.msgtouch.network.socket.session.ISession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Dean on 2017/1/6.
 */
public class ASyncRpcCallBack<T>   implements RpcCallBack<T> {
    private static Logger logger= LoggerFactory.getLogger(ASyncRpcCallBack.class);

    public  void processResult(ISession session, T result){
        logger.info("ASyncRpcCallBack processResult ----");
    }

    public  void processException(Throwable throwable){
        throwable.printStackTrace();
        logger.error("ASyncRpcCallBack processException throwable={}",throwable.getMessage());
    }


    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }
}
