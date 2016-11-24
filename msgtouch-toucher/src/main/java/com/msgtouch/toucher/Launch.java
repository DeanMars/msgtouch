package com.msgtouch.toucher;

import com.msgtouch.framework.Bootstrap;
import com.msgtouch.framework.consul.ConsulEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.util.UUID;

/**
 * Created by Dean on 2016/10/9.
 */
@SpringBootApplication
public class Launch {

    private  static Logger logger= LoggerFactory.getLogger(Launch.class);

    public static void main(String []args){

        ApplicationContext applicationContext=SpringApplication.run(Launch.class,args);
        Environment environment=applicationContext.getEnvironment();
        String profileName=environment.getProperty("my.profile.name");

        logger.info("MsgTouch Server Launch ApplicationContext Environment profile={}",profileName);




        Bootstrap.getInstances().startServerSocket(applicationContext);

        logger.info("Launch success !");

    }

}
