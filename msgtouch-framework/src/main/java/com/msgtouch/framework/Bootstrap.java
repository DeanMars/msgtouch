package com.msgtouch.framework;


import com.msgtouch.framework.consul.ConsulEngine;
import com.msgtouch.framework.context.SpringBeanAccess;
import com.msgtouch.framework.settings.SettingsBuilder;
import com.msgtouch.framework.settings.SocketServerSetting;
import com.msgtouch.framework.socket.SocketEngine;
import com.msgtouch.framework.socket.dispatcher.JsonPacketMethodDispatcher;
import com.msgtouch.framework.socket.dispatcher.MsgTouchServiceEngine;
import com.msgtouch.framework.socket.dispatcher.PBPacketMethodDispatcher;
import com.msgtouch.framework.utils.RemoteUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

/**
 * Created by Dean on 2016/9/5.
 */
public class Bootstrap {

    private static Bootstrap bootstrap=null;

    private Bootstrap(){}

    public static Bootstrap getInstances(){
        if(null==bootstrap){
            synchronized (Bootstrap.class){
                if(null==bootstrap){
                    bootstrap=new Bootstrap();
                }
            }
        }
        return bootstrap;
    }

    public void initContext(ApplicationContext applicationContext){
        //上下文
        SpringBeanAccess.getInstances().initSpringContext(applicationContext);
        //初始化配置
        SettingsBuilder.init(applicationContext);
    }


    public synchronized void startPBServerSocket(ApplicationContext applicationContext){
        //初始化上下文
        initContext(applicationContext);
        SocketServerSetting setting= SettingsBuilder.getSocketServerSetting(applicationContext);
        PBPacketMethodDispatcher msgTouchMethodDispatcher=new PBPacketMethodDispatcher(setting.cmdThreadSize);
        MsgTouchServiceEngine.getInstances().loadService(msgTouchMethodDispatcher);
        //consul 服务注册
        ConsulEngine.getInstance().bind(applicationContext);
        ConsulEngine.getInstance().registeService(msgTouchMethodDispatcher.getClusterlist());
        //启动netty toucher
        SocketEngine.startPBPacketServer(setting,msgTouchMethodDispatcher);

    }

    public synchronized void startJsonServerSocket(ApplicationContext applicationContext){
        //初始化上下文
        initContext(applicationContext);
        SocketServerSetting setting= SettingsBuilder.getSocketServerSetting(applicationContext);

        JsonPacketMethodDispatcher msgTouchMethodDispatcher=new JsonPacketMethodDispatcher(setting.cmdThreadSize);
        MsgTouchServiceEngine.getInstances().loadService(msgTouchMethodDispatcher);
        //consul 服务注册
        //ConsulEngine.getInstance().bind(applicationContext,msgTouchMethodDispatcher);


        //连接注册中心
        //ZooKeeperEngine.getInstances().start(applicationContext);
        //启动netty toucher
        SocketEngine.startJsonPacketServer(setting,msgTouchMethodDispatcher);

    }

    public synchronized void startClientSocket(ApplicationContext applicationContext){
        //初始化上下文
        initContext(applicationContext);
        //rpc service加载
        List<String> rpcServiceList=RemoteUtils.getRpcServices((ConfigurableApplicationContext)applicationContext);


       // ConsulEngine.getInstance().findServices(rpcServiceList);

        /*SocketClientSetting socketClientSetting=new SocketClientSetting();
        socketClientSetting.host="127.0.0.1";
        socketClientSetting.port=8001;
        socketClientSetting.timeOutSecond=20;

        try {
            SocketEngine.startClient(socketClientSetting);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

}
