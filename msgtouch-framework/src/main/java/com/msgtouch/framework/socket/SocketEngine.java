package com.msgtouch.framework.socket;

import com.msgtouch.framework.settings.SocketServerSetting;
import com.msgtouch.framework.socket.client.MsgTouchClientApi;
import com.msgtouch.framework.socket.client.SocketClientEngine;
import com.msgtouch.framework.settings.SocketClientSetting;
import com.msgtouch.framework.socket.dispatcher.JsonPacketMethodDispatcher;
import com.msgtouch.framework.socket.server.SocketServerEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Dean on 2016/9/5.
 */
public class SocketEngine {
    private static Logger logger= LoggerFactory.getLogger(SocketEngine.class);

    public static void startServer( SocketServerSetting setting,JsonPacketMethodDispatcher msgTouchMethodDispatcher){
        //msg service加载
        logger.info("SocketEngine startServer bossThreadSize={},cmdThreadSize={},workerThreadSize={},port={}",
                setting.bossThreadSize, setting.cmdThreadSize,setting.workerThreadSize,setting.port);
        new SocketServerEngine(setting).bind(msgTouchMethodDispatcher);
    }


    public static MsgTouchClientApi startClient(SocketClientSetting socketClientSetting)throws Exception{
        JsonPacketMethodDispatcher msgTouchMethodDispatcher=new JsonPacketMethodDispatcher(socketClientSetting.cmdThreadSize,true);
        SocketClientEngine socketClientEngine=new SocketClientEngine(socketClientSetting,msgTouchMethodDispatcher);
        socketClientEngine.bind();
        return MsgTouchClientApi.getInstance().initComponents(socketClientEngine);
    }


    /*public static MsgTouchClientApi startClient()throws Exception{

        SocketClientEngine socketClientEngine=new SocketClientEngine(socketClientSetting);
        JsonPacketMethodDispatcher msgTouchMethodDispatcher=new JsonPacketMethodDispatcher();
        socketClientEngine.bind(msgTouchMethodDispatcher);
        return MsgTouchClientApi.getInstance().initComponents(socketClientEngine);

    }
*/


}
