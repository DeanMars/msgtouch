package com.msgtouch.framework.socket.client;

import com.msgtouch.framework.socket.client.proxy.CglibRpcCallProxyFactory;
import com.msgtouch.framework.socket.dispatcher.MsgPushedListener;
import com.msgtouch.framework.socket.packet.MsgPacket;
import com.msgtouch.framework.socket.session.ISession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by Dean on 2016/11/23.
 */
public class MsgTouchClientApi {
    private  static Logger logger= LoggerFactory.getLogger(MsgTouchClientApi.class);

    private CglibRpcCallProxyFactory rpcCallProxyFactory=null;
    private SocketClientEngine socketClientEngine=null;
    private static final MsgTouchClientApi msgTouchClientApi=new MsgTouchClientApi();
    private int syncCallTimeout=10;
    private MsgTouchClientApi(){}

    public static MsgTouchClientApi getInstance(){
        return msgTouchClientApi;
    }

    public MsgTouchClientApi initComponents(SocketClientEngine socketClientEngine){
        syncCallTimeout=socketClientEngine.getSettings().timeOutSecond;
        this.socketClientEngine=socketClientEngine;
        this.rpcCallProxyFactory=CglibRpcCallProxyFactory.getInstance();
        return msgTouchClientApi;
    }


    public <T> T getRpcCallProxy(boolean sync,Class<T> clazz) {
        return rpcCallProxyFactory.getRpcCallProxy(sync,clazz);
    }


    public <T> T syncRpcCall(String clusterName, String cmd, Class<T> resultType, Object... params) throws Exception{
        MsgPacket packet=new MsgPacket(cmd,params);
        long before= System.currentTimeMillis();
        T result=getSession().syncRpcSend(packet, resultType, (long) syncCallTimeout, TimeUnit.SECONDS);
        long after=System.currentTimeMillis();
        logger.info("Rpc sync call:cmd ={},responseTime = {}",cmd,after-before);
        return result;
    }

    public ISession getSession(){
        return  socketClientEngine.getSession();
    }


    public void addPushedListener(MsgPushedListener msgPushedListener){
        socketClientEngine.getMsgTouchMethodDispatcher().addPushedListener(msgPushedListener);
    }

}
