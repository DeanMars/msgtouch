package com.msgtouch.broker;

import com.msgtouch.broker.utils.RouteManager;
import com.msgtouch.common.service.TestService;
import com.msgtouch.framework.consul.ConsulEngine;
import com.msgtouch.framework.utils.RemoteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Created by Dean on 2016/10/9.
 */
@EnableAutoConfiguration
@Configuration
@ComponentScan
public class Launch {

    private  static Logger logger= LoggerFactory.getLogger(Launch.class);

    public static void main(String []args){

        ApplicationContext applicationContext=SpringApplication.run(Launch.class,args);

        Environment environment=applicationContext.getEnvironment();
        String profileName=environment.getProperty("my.profile.name");

        logger.info("MsgTouch Server Launch ApplicationContext Environment profile={}",profileName);

        logger.info("Launch success !");

        RouteManager.init(applicationContext);

    }

}
