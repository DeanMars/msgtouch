package com.msgtouch.framework.socket;

import com.msgtouch.framework.settings.SettingsBuilder;
import com.msgtouch.framework.settings.SocketServerSetting;
import com.msgtouch.framework.socket.client.MsgTouchClientApi;
import com.msgtouch.framework.socket.client.SocketClientEngine;
import com.msgtouch.framework.settings.SocketClientSetting;
import com.msgtouch.framework.socket.dispatcher.MsgTouchMethodDispatcher;
import com.msgtouch.framework.socket.dispatcher.MsgTouchServiceEngine;
import com.msgtouch.framework.socket.server.SocketServerEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * Created by Dean on 2016/9/5.
 */
public class SocketEngine {
    private static Logger logger= LoggerFactory.getLogger(SocketEngine.class);

    public static void startServer(ApplicationContext applicationContext,MsgTouchMethodDispatcher msgTouchMethodDispatcher){
        //msg service加载
        SocketServerSetting setting= SettingsBuilder.getSocketServerSetting(applicationContext);
        logger.info("SocketEngine startServer bossThreadSize={},cmdThreadSize={},workerThreadSize={},port={}",
                setting.bossThreadSize, setting.cmdThreadSize,setting.workerThreadSize,setting.port);
        new SocketServerEngine(setting).bind(msgTouchMethodDispatcher);
    }


    public static MsgTouchClientApi startClient(SocketClientSetting socketClientSetting)throws Exception{
        SocketClientEngine socketClientEngine=new SocketClientEngine(socketClientSetting);
        MsgTouchMethodDispatcher msgTouchMethodDispatcher=new MsgTouchMethodDispatcher();
        socketClientEngine.bind(msgTouchMethodDispatcher);
        return MsgTouchClientApi.getInstance().initComponents(socketClientEngine);
    }


    /*public static MsgTouchClientApi startClient()throws Exception{

        SocketClientEngine socketClientEngine=new SocketClientEngine(socketClientSetting);
        MsgTouchMethodDispatcher msgTouchMethodDispatcher=new MsgTouchMethodDispatcher();
        socketClientEngine.bind(msgTouchMethodDispatcher);
        return MsgTouchClientApi.getInstance().initComponents(socketClientEngine);

    }
*/


}
