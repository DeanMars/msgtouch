package com.msgtouch.framework.socket;

import com.msgtouch.framework.context.Constraint;
import com.msgtouch.framework.socket.client.SocketClientEngine;
import com.msgtouch.framework.socket.client.SocketClientSetting;
import com.msgtouch.framework.socket.dispatcher.MsgTouchMethodDispatcher;
import com.msgtouch.framework.socket.server.SocketServerEngine;
import com.msgtouch.framework.socket.server.SocketServerSetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * Created by Dean on 2016/9/5.
 */
public class SocketEngine {
    private static Logger logger= LoggerFactory.getLogger(SocketEngine.class);

    public static void startServer(MsgTouchMethodDispatcher msgTouchMethodDispatcher,ApplicationContext applicationContext){
        SocketServerSetting setting=getSocketServerSetting(applicationContext);
        logger.info("SocketEngine startServer bossThreadSize={},cmdThreadSize={},workerThreadSize={},port={}",
                setting.bossThreadSize, setting.cmdThreadSize,setting.workerThreadSize,setting.port);
        new SocketServerEngine(setting).bind(msgTouchMethodDispatcher);
    }


    public static void startClient(SocketClientSetting socketClientSetting)throws Exception{
        SocketClientEngine SocketClientEngine=new SocketClientEngine(socketClientSetting);
        SocketClientEngine.startSocket();
    }


    private static SocketServerSetting getSocketServerSetting(ApplicationContext applicationContext){
        Environment environment=applicationContext.getEnvironment();
        SocketServerSetting setting=new SocketServerSetting();
        if(environment.containsProperty(Constraint.TCP_SERVER_BOSSTHREADSIZE)){
            setting.bossThreadSize=Integer.parseInt(environment.getProperty(Constraint.TCP_SERVER_BOSSTHREADSIZE));
        }
        if(environment.containsProperty(Constraint.TCP_SERVER_CMDTHREADSIZE)){
            setting.cmdThreadSize=Integer.parseInt(environment.getProperty(Constraint.TCP_SERVER_CMDTHREADSIZE));
        }
        if(environment.containsProperty(Constraint.TCP_SERVER_WORKERTHREADSIZE)) {
            setting.workerThreadSize = Integer.parseInt(environment.getProperty(Constraint.TCP_SERVER_WORKERTHREADSIZE));
        }

        if(!environment.containsProperty(Constraint.TCP_SERVER_PORT)){
            throw new IllegalArgumentException("SocketServerEngine msgtouch.tcp.toucher.port is needed ");
        }
        setting.port=Integer.parseInt(environment.getProperty(Constraint.TCP_SERVER_PORT));



        return setting;
    }

}
