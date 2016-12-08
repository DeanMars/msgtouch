package com.msgtouch.framework.socket;

import com.msgtouch.framework.settings.SocketServerSetting;
import com.msgtouch.framework.socket.client.MsgTouchClientApi;
import com.msgtouch.framework.socket.client.SocketClientEngine;
import com.msgtouch.framework.settings.SocketClientSetting;
import com.msgtouch.framework.socket.codec.*;
import com.msgtouch.framework.socket.dispatcher.JsonPacketMethodDispatcher;
import com.msgtouch.framework.socket.dispatcher.PBPacketMethodDispatcher;
import com.msgtouch.framework.socket.server.SocketServerEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Dean on 2016/9/5.
 */
public class SocketEngine {
    private static Logger logger= LoggerFactory.getLogger(SocketEngine.class);

    public static void startJsonPacketServer( SocketServerSetting setting,JsonPacketMethodDispatcher msgTouchMethodDispatcher){
        //msg service加载
        logger.info("SocketEngine startJsonPacketServer bossThreadSize={},cmdThreadSize={},workerThreadSize={},port={}",
                setting.bossThreadSize, setting.cmdThreadSize,setting.workerThreadSize,setting.port);
        MsgDecoder msgTouchDecoder=new JsonDecoder();
        MsgEncoder msgTouchEncoder=new JsonEncoder();
        new SocketServerEngine(setting,msgTouchMethodDispatcher).bind(msgTouchDecoder,msgTouchEncoder);
    }

    public static void startPBPacketServer( SocketServerSetting setting,PBPacketMethodDispatcher msgTouchMethodDispatcher){
        //msg service加载
        logger.info("SocketEngine startPBPacketServer bossThreadSize={},cmdThreadSize={},workerThreadSize={},port={}",
                setting.bossThreadSize, setting.cmdThreadSize,setting.workerThreadSize,setting.port);
        MsgDecoder msgTouchDecoder=new PBDecoder();
        MsgEncoder msgTouchEncoder=new PBEncoder();
        new SocketServerEngine(setting,msgTouchMethodDispatcher).bind(msgTouchDecoder,msgTouchEncoder);
    }


    public static MsgTouchClientApi startJsonPacketClient(SocketClientSetting socketClientSetting)throws Exception{
        JsonPacketMethodDispatcher msgTouchMethodDispatcher=new JsonPacketMethodDispatcher(socketClientSetting.cmdThreadSize,true);
        SocketClientEngine socketClientEngine=new SocketClientEngine(socketClientSetting,msgTouchMethodDispatcher);
        MsgDecoder msgTouchDecoder=new JsonDecoder();
        MsgEncoder msgTouchEncoder=new JsonEncoder();
        socketClientEngine.bind(msgTouchDecoder,msgTouchEncoder);
        return MsgTouchClientApi.getInstance().initComponents(socketClientEngine);
    }


    public static MsgTouchClientApi startPBPacketClient(SocketClientSetting socketClientSetting)throws Exception{
        PBPacketMethodDispatcher msgTouchMethodDispatcher=new PBPacketMethodDispatcher(socketClientSetting.cmdThreadSize);
        SocketClientEngine socketClientEngine=new SocketClientEngine(socketClientSetting,msgTouchMethodDispatcher);
        MsgDecoder msgTouchDecoder=new PBDecoder();
        MsgEncoder msgTouchEncoder=new PBEncoder();
        socketClientEngine.bind(msgTouchDecoder,msgTouchEncoder);
        return MsgTouchClientApi.getInstance().initComponents(socketClientEngine);
    }



}
