package com.msgtouch.framework.context;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;

/**
 * Created by Dean on 2016/9/8.
 */
public class SpringBeanAccess {
    private ApplicationContext applicationContext;
    private static SpringBeanAccess springBeanAccess=null;

    private SpringBeanAccess(){}

    public static SpringBeanAccess getInstances(){
        if(null==springBeanAccess){
            synchronized (SpringBeanAccess.class){
                if(null==springBeanAccess){
                    springBeanAccess=new SpringBeanAccess();
                }
            }
        }
        return springBeanAccess;
    }

    public synchronized void initSpringContext(ApplicationContext applicationContext){
        this.applicationContext=applicationContext;
    }

    public <T> T getBean(Class<T> clazz) throws IllegalAccessException, InstantiationException {
        return applicationContext.getBean(clazz);
    }

    public ApplicationContext getApplicationContext(){
        return applicationContext;
    }



}
