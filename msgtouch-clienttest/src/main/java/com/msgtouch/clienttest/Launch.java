package com.msgtouch.clienttest;

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



        logger.info("Launch success !");

    }

}
