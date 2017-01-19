package com.msgtouch.framework;


import com.msgtouch.framework.registry.ConsulEngine;
import com.msgtouch.framework.setting.SettingsBuilder;
import com.msgtouch.framework.utils.RemoteUtils;
import com.msgtouch.network.context.Context;
import com.msgtouch.network.context.SpringBeanAccess;
import com.msgtouch.network.settings.SocketServerSetting;
import com.msgtouch.network.socket.NetServerEngine;
import com.msgtouch.network.socket.dispatcher.JsonPacketMethodDispatcher;
import com.msgtouch.network.socket.dispatcher.PBPacketMethodDispatcher;
import com.msgtouch.network.socket.server.MsgTouchServiceEngine;
import com.msgtouch.network.socket.session.SessionManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

/**
 * Created by Dean on 2016/9/5.
 */
public class Bootstrap {

    private Bootstrap(){}

    public static class BootstrapHolder{
        private static final Bootstrap bootstrap=new Bootstrap();
    }
    public static Bootstrap getInstances(){
        return BootstrapHolder.bootstrap;
    }

    public void initContext(ApplicationContext applicationContext){
        //上下文
        SpringBeanAccess.getInstances().initSpringContext(applicationContext);
        Context.initBeanAccess(SpringBeanAccess.getInstances());
        SessionManager.getInstance().initRegistryEngine(ConsulEngine.getInstance());
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
        // ConsulEngine.getInstance().bind(applicationContext);
        //ConsulEngine.getInstance().registeService(msgTouchMethodDispatcher.getClusterlist());
        //启动netty toucher
        NetServerEngine.startPBPacketServer(setting,msgTouchMethodDispatcher);

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
        NetServerEngine.startJsonPacketServer(setting,msgTouchMethodDispatcher);

    }



}
