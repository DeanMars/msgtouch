package com.msgtouch.network.socket;

import com.msgtouch.network.settings.SocketClientSetting;
import com.msgtouch.network.socket.client.MsgTouchClientApi;
import com.msgtouch.network.socket.client.SimpleMsgTouchClientApi;
import com.msgtouch.network.socket.client.SocketClientEngine;
import com.msgtouch.network.socket.codec.*;
import com.msgtouch.network.socket.dispatcher.JsonPacketMethodDispatcher;
import com.msgtouch.network.socket.dispatcher.PBPacketMethodDispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Dean on 2016/9/5.
 */
public class NetClientEngine {
    private static Logger logger= LoggerFactory.getLogger(NetClientEngine.class);

    public static MsgTouchClientApi startJsonPacketClient(SocketClientSetting socketClientSetting)throws Exception{
        JsonPacketMethodDispatcher msgTouchMethodDispatcher=new JsonPacketMethodDispatcher(socketClientSetting.cmdThreadSize,true);
        SocketClientEngine socketClientEngine=new SocketClientEngine(socketClientSetting,msgTouchMethodDispatcher);
        MsgDecoder msgTouchDecoder=new JsonDecoder();
        MsgEncoder msgTouchEncoder=new JsonEncoder();
        socketClientEngine.bind(msgTouchDecoder,msgTouchEncoder);
        MsgTouchClientApi msgTouchClientApi=new MsgTouchClientApi();
        return msgTouchClientApi.initComponents(socketClientEngine);
    }


    public static MsgTouchClientApi startPBPacketClient(SocketClientSetting socketClientSetting)throws Exception{
        PBPacketMethodDispatcher msgTouchMethodDispatcher=new PBPacketMethodDispatcher(socketClientSetting.cmdThreadSize);
        SocketClientEngine socketClientEngine=new SocketClientEngine(socketClientSetting,msgTouchMethodDispatcher);
        MsgDecoder msgTouchDecoder=new PBDecoder();
        MsgEncoder msgTouchEncoder=new PBEncoder();
        socketClientEngine.bind(msgTouchDecoder,msgTouchEncoder);
        MsgTouchClientApi msgTouchClientApi=new MsgTouchClientApi();
        return msgTouchClientApi.initComponents(socketClientEngine);
    }

    public static MsgTouchClientApi startSimpleJsonPacketClient(SocketClientSetting socketClientSetting)throws Exception{
        JsonPacketMethodDispatcher msgTouchMethodDispatcher=new JsonPacketMethodDispatcher(socketClientSetting.cmdThreadSize,true);
        SocketClientEngine socketClientEngine=new SocketClientEngine(socketClientSetting,msgTouchMethodDispatcher);
        MsgDecoder msgTouchDecoder=new JsonDecoder();
        MsgEncoder msgTouchEncoder=new JsonEncoder();
        socketClientEngine.bind(msgTouchDecoder,msgTouchEncoder);
        return SimpleMsgTouchClientApi.getInstance().initComponents(socketClientEngine);
    }


    public static MsgTouchClientApi startSimplePBPacketClient(SocketClientSetting socketClientSetting)throws Exception{
        PBPacketMethodDispatcher msgTouchMethodDispatcher=new PBPacketMethodDispatcher(socketClientSetting.cmdThreadSize);
        SocketClientEngine socketClientEngine=new SocketClientEngine(socketClientSetting,msgTouchMethodDispatcher);
        MsgDecoder msgTouchDecoder=new PBDecoder();
        MsgEncoder msgTouchEncoder=new PBEncoder();
        socketClientEngine.bind(msgTouchDecoder,msgTouchEncoder);
        return SimpleMsgTouchClientApi.getInstance().initComponents(socketClientEngine);
    }

}
