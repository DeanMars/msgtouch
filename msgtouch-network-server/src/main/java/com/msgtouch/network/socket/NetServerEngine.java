package com.msgtouch.network.socket;

import com.msgtouch.network.settings.SocketServerSetting;
import com.msgtouch.network.socket.codec.*;
import com.msgtouch.network.socket.dispatcher.JsonPacketMethodDispatcher;
import com.msgtouch.network.socket.dispatcher.PBPacketMethodDispatcher;
import com.msgtouch.network.socket.server.SocketServerEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Dean on 2016/9/5.
 */
public class NetServerEngine {
    private static Logger logger= LoggerFactory.getLogger(NetServerEngine.class);

    public static void startJsonPacketServer(SocketServerSetting setting, JsonPacketMethodDispatcher msgTouchMethodDispatcher){
        //msg service加载
        logger.info("NetServerEngine startJsonPacketServer bossThreadSize={},cmdThreadSize={},workerThreadSize={},port={}",
                setting.bossThreadSize, setting.cmdThreadSize,setting.workerThreadSize,setting.port);
        MsgDecoder msgTouchDecoder=new JsonDecoder();
        MsgEncoder msgTouchEncoder=new JsonEncoder();
        new SocketServerEngine(setting,msgTouchMethodDispatcher).bind(msgTouchDecoder,msgTouchEncoder);
    }

    public static void startPBPacketServer( SocketServerSetting setting,PBPacketMethodDispatcher msgTouchMethodDispatcher){
        //msg service加载
        logger.info("NetServerEngine startPBPacketServer bossThreadSize={},cmdThreadSize={},workerThreadSize={},port={}",
                setting.bossThreadSize, setting.cmdThreadSize,setting.workerThreadSize,setting.port);
        MsgDecoder msgTouchDecoder=new PBDecoder();
        MsgEncoder msgTouchEncoder=new PBEncoder();
        new SocketServerEngine(setting,msgTouchMethodDispatcher).bind(msgTouchDecoder,msgTouchEncoder);
    }




}
