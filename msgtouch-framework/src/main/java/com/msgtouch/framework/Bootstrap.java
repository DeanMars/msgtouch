package com.msgtouch.framework;


import com.msgtouch.framework.consul.ConsulEngine;
import com.msgtouch.framework.context.Constraint;
import com.msgtouch.framework.settings.ContextSetting;
import com.msgtouch.framework.context.SpringBeanAccess;
import com.msgtouch.framework.settings.SettingsBuilder;
import com.msgtouch.framework.socket.SocketEngine;
import com.msgtouch.framework.socket.dispatcher.MsgTouchMethodDispatcher;
import com.msgtouch.framework.socket.dispatcher.MsgTouchServiceEngine;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

/**
 * Created by Dean on 2016/9/5.
 */
public class Bootstrap {
    private ApplicationContext springContext=null;

    private ContextSetting contextSetting=null;

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

    private void initSpringContext(ApplicationContext applicationContext){
        SpringBeanAccess.getInstances().initSpringContext(applicationContext);
    }


    public synchronized void startServerSocket(ApplicationContext applicationContext){
        //初始化上下文
        initSpringContext(applicationContext);
        //初始化配置
        SettingsBuilder.init(applicationContext);

        //msg service加载
        MsgTouchMethodDispatcher msgTouchMethodDispatcher= MsgTouchServiceEngine.getInstances().loadService();

        //consul 服务注册
        //ConsulEngine.getInstance().bind(applicationContext,msgTouchMethodDispatcher);
        //连接注册中心
        //ZooKeeperEngine.getInstances().start(applicationContext);
        //启动netty toucher
        SocketEngine.startServer(msgTouchMethodDispatcher,applicationContext);


    }

    public synchronized void startClientSocket(){

    }

}
