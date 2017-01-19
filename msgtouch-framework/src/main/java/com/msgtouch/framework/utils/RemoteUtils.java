package com.msgtouch.framework.utils;

import com.msgtouch.network.annotation.MsgService;
import com.msgtouch.network.annotation.RpcService;
import com.msgtouch.network.socket.client.CglibRpcCallProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public static List<String>  getRpcServices(ConfigurableApplicationContext applicationContext,CglibRpcCallProxyFactory cglibRpcCallProxyFactory){
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
                        MsgService msgService=(MsgService)fieldClass.getAnnotation(MsgService.class);
                        remoteService.add(msgService.value());
                        logger.debug("RemoteUtils getRpcServices RpcService={} "+fieldClass.getName());
                        Object classValue= cglibRpcCallProxyFactory.getRpcCallProxy(true,fieldClass);
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
