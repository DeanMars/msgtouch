package com.msgtouch.network.socket.client;

import com.msgtouch.network.socket.dispatcher.ASyncRpcCallBack;
import com.msgtouch.network.socket.dispatcher.RpcCallBack;
import com.msgtouch.network.socket.listener.MsgPushedListener;
import com.msgtouch.network.socket.packet.MsgPBPacket;
import com.msgtouch.network.socket.packet.MsgPacket;
import com.msgtouch.network.socket.session.ISession;
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
        T result=getSession().syncRpcSend(packet, (long) syncCallTimeout, TimeUnit.SECONDS);
        long after=System.currentTimeMillis();
        logger.info("Rpc sync call:cmd ={},responseTime = {}",cmd,after-before);
        return result;
    }

    public <T> void asyncRpcCall(String clusterName, String cmd, Class<T> resultType, RpcCallBack rpcCallBack, Object... params) throws Exception{
        MsgPacket packet=new MsgPacket(cmd,params);
        getSession().asyncRpcSend(packet,rpcCallBack);
    }

    public MsgPBPacket.Packet.Builder syncRpcCall(String clusterName, String cmd, MsgPBPacket.Packet.Builder builder) throws Exception{
        long before= System.currentTimeMillis();
        builder.setCmd(clusterName+"/"+cmd);
        MsgPBPacket.Packet.Builder result=getSession().syncRpcSend(builder, (long) syncCallTimeout, TimeUnit.SECONDS);
        long after=System.currentTimeMillis();
        logger.info("Rpc sync call:cmd ={},responseTime = {}",cmd,after-before);
        return result;
    }

    public void asyncRpcCall(String clusterName, String cmd, MsgPBPacket.Packet.Builder builder, RpcCallBack rpcCallBack) throws Exception{
        builder.setCmd(clusterName+"/"+cmd);
        getSession().asyncPushPBMsg(builder,rpcCallBack);
    }

    public ISession getSession(){
        return  socketClientEngine.getSession();
    }


    public void addPushedListener(MsgPushedListener msgPushedListener){
        socketClientEngine.getMsgTouchMethodDispatcher().addPushedListener(msgPushedListener);
    }

}
