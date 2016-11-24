package com.msgtouch.framework.utils;

import com.msgtouch.framework.annotation.RpcService;
import com.msgtouch.framework.consul.ConsulEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dean on 2016/11/21.
 */
public class RemoteUtils {

    private  static Logger logger= LoggerFactory.getLogger(RemoteUtils.class);

    public static void getRpcServices(ConfigurableApplicationContext applicationContext){

        DefaultListableBeanFactory defaultListableBeanFactory=(DefaultListableBeanFactory)applicationContext.getBeanFactory();
        String [] beanNames=defaultListableBeanFactory.getBeanDefinitionNames();
        List<String> remoteService=new ArrayList<String>();
        for(String beanName:beanNames){
            Object beanObj=defaultListableBeanFactory.getBean(beanName);
            Field[] fields=beanObj.getClass().getDeclaredFields();
            for(Field field:fields){
                if(ClassUtils.hasAnnotation(field, RpcService.class)){
                    logger.debug("RemoteUtils getRpcServices RpcService={} "+field.getName());
                    Class fieldClass=field.getType();
                    remoteService.add(fieldClass.getName());
                    boolean flag=defaultListableBeanFactory.containsBean(fieldClass.getName());
                    if(!flag){
                        BeanDefinitionBuilder beanDefinitionBuilder= BeanDefinitionBuilder.rootBeanDefinition(fieldClass);
                        defaultListableBeanFactory.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
                    }
                }
            }


        }
        if(remoteService.size()>0){
            ConsulEngine.getInstance().registeConsumer();
        }

    }
}
