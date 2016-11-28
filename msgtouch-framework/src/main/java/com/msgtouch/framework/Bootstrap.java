package com.msgtouch.framework;


import com.msgtouch.framework.context.SpringBeanAccess;
import com.msgtouch.framework.settings.SettingsBuilder;
import com.msgtouch.framework.settings.SocketClientSetting;
import com.msgtouch.framework.socket.SocketEngine;
import com.msgtouch.framework.socket.dispatcher.MsgTouchMethodDispatcher;
import com.msgtouch.framework.socket.dispatcher.MsgTouchServiceEngine;
import com.msgtouch.framework.utils.RemoteUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

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

    private void initContext(ApplicationContext applicationContext){
        //初始化上下文
        SpringBeanAccess.getInstances().initSpringContext(applicationContext);
        //初始化配置
        SettingsBuilder.init(applicationContext);
    }


    public synchronized void startServerSocket(ApplicationContext applicationContext){
        //初始化上下文
        initContext(applicationContext);

        //consul 服务注册
        //ConsulEngine.getInstance().bind(applicationContext,msgTouchMethodDispatcher);
        //连接注册中心
        //ZooKeeperEngine.getInstances().start(applicationContext);
        //启动netty toucher
        SocketEngine.startServer(applicationContext);

    }

    public synchronized void startClientSocket(ApplicationContext applicationContext){
        //初始化上下文
        initContext(applicationContext);
        //rpc service加载
        RemoteUtils.getRpcServices((ConfigurableApplicationContext)applicationContext);

        SocketClientSetting socketClientSetting=new SocketClientSetting();
        socketClientSetting.host="127.0.0.1";
        socketClientSetting.port=8001;
        socketClientSetting.timeOutSecond=20;

        try {
            SocketEngine.startClient(socketClientSetting);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
