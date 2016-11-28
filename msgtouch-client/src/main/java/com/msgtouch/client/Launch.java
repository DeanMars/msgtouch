package com.msgtouch.client;

import com.msgtouch.client.service.ClientService;
import com.msgtouch.common.service.LoginService;
import com.msgtouch.framework.Bootstrap;
import com.msgtouch.framework.settings.SocketClientSetting;
import com.msgtouch.framework.socket.SocketEngine;
import com.msgtouch.framework.socket.client.MsgTouchClientApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

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


        Bootstrap.getInstances().startClientSocket(applicationContext);


        ClientService clientService=applicationContext.getBean(ClientService.class);
        String ret=clientService.test();
        logger.info("ret={}",ret);

        logger.info("Launch success !");

    }

}
