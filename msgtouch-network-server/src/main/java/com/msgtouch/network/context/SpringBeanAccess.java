package com.msgtouch.network.context;

import org.springframework.context.ApplicationContext;

/**
 * Created by Dean on 2016/9/8.
 */
public class SpringBeanAccess implements IBeanAccess{
    private ApplicationContext applicationContext;

    private SpringBeanAccess(){}
    private static class SpringBeanAccessHolder{
        private static final SpringBeanAccess springBeanAccess=new SpringBeanAccess();
    }
    public static SpringBeanAccess getInstances(){
        return SpringBeanAccessHolder.springBeanAccess;
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
