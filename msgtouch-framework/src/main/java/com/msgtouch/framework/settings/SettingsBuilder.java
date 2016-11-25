package com.msgtouch.framework.settings;

import com.msgtouch.framework.context.Constraint;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

/**
 * Created by Dean on 2016/11/23.
 */
public class SettingsBuilder {

    private static Environment environment=null;

    private static ContextSetting contextSetting;

    private static ZookeeperSetting zookeeperSetting=null;


    public static void init(ApplicationContext applicationContext){
        if(null==environment){
            environment=applicationContext.getEnvironment();
        }
    }

    public static ContextSetting buildContextSetting() {
        if (null == contextSetting) {
            contextSetting = new ContextSetting();
            if (environment.containsProperty(Constraint.APP_NAME)) {
                contextSetting.APP_NAME = environment.getProperty(Constraint.APP_NAME);
            }
            if (environment.containsProperty(Constraint.APP_VERSION)) {
                contextSetting.APP_VERSION = environment.getProperty(Constraint.APP_VERSION);
            }
            if (environment.containsProperty(Constraint.LOCAL_IP)) {
                contextSetting.LOCAL_IP = environment.getProperty(Constraint.LOCAL_IP);
            }
            if (environment.containsProperty(Constraint.APP_EXT)) {
                contextSetting.APP_EXT = environment.getProperty(Constraint.APP_EXT);
            }
        }
        return contextSetting;
    }

    public static ZookeeperSetting buildZookeeperSetting() {
        if (null == contextSetting) {
            zookeeperSetting = new ZookeeperSetting();
            if (environment.containsProperty(Constraint.ZOOKEEPER_CONNECTSTRING)) {
                zookeeperSetting.connectString = environment.getProperty(Constraint.ZOOKEEPER_CONNECTSTRING);
            }
            if (environment.containsProperty(Constraint.ZOOKEEPER_SESSIONTIMEOUT)) {
                zookeeperSetting.timeout = Integer.parseInt(environment.getProperty(Constraint.ZOOKEEPER_SESSIONTIMEOUT));
            }
        }
        return zookeeperSetting;
    }

    public static SocketServerSetting getSocketServerSetting(ApplicationContext applicationContext){
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
