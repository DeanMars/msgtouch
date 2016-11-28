package com.msgtouch.framework.utils;

import com.msgtouch.framework.annotation.RpcService;
import com.msgtouch.framework.consul.ConsulEngine;
import com.msgtouch.framework.socket.client.proxy.CglibRpcCallProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ConfigurableApplicationContext;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dean on 2016/11/21.
 */
public class RemoteUtils {

    private  static Logger logger= LoggerFactory.getLogger(RemoteUtils.class);

    public static List<String>  getRpcServices(ConfigurableApplicationContext applicationContext){
        List<String> remoteService=new ArrayList<String>();
        try {
            DefaultListableBeanFactory defaultListableBeanFactory=(DefaultListableBeanFactory)applicationContext.getBeanFactory();
            String [] beanNames=defaultListableBeanFactory.getBeanDefinitionNames();
            for(String beanName:beanNames){
                Object beanObj=defaultListableBeanFactory.getBean(beanName);
                Field[] fields=beanObj.getClass().getDeclaredFields();
                for(Field field:fields){
                    if(ClassUtils.hasAnnotation(field, RpcService.class)){
                        Class fieldClass=field.getType();
                        remoteService.add(fieldClass.getName());
                        logger.debug("RemoteUtils getRpcServices RpcService={} "+fieldClass.getName());
                        Object classValue=CglibRpcCallProxyFactory.getInstance().getRpcCallProxy(true,fieldClass);
                        field.setAccessible(true);
                        field.set(beanObj,classValue);
                        field.setAccessible(false);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return remoteService;
    }
}
