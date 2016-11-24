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




}
