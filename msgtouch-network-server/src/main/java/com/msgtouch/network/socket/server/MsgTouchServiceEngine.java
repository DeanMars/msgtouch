package com.msgtouch.network.socket.server;


import com.msgtouch.network.annotation.MsgMethod;
import com.msgtouch.network.annotation.MsgService;
import com.msgtouch.network.context.SpringBeanAccess;
import com.msgtouch.network.socket.dispatcher.MethodDispatcher;
import com.msgtouch.network.socket.dispatcher.MsgTouchMethodInvoker;
import com.msgtouch.network.utils.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dean on 2016/9/8.
 */
public class MsgTouchServiceEngine {

    private static Logger logger= LoggerFactory.getLogger(MsgTouchServiceEngine.class);


    private MsgTouchServiceEngine(){}

    private static class MsgTouchServiceEngineHolder{
        private static final MsgTouchServiceEngine serviceEngine=new MsgTouchServiceEngine();
    }


    public static MsgTouchServiceEngine getInstances(){
        return MsgTouchServiceEngineHolder.serviceEngine;
    }



    public MethodDispatcher loadService(MethodDispatcher msgTouchMethodDispatcher){
        String [] beanNames= SpringBeanAccess.getInstances().getApplicationContext().getBeanDefinitionNames();
        for(String beanName:beanNames){
            Object controlClass= SpringBeanAccess.getInstances().getApplicationContext().getBean(beanName);
            Class [] interfaces=controlClass.getClass().getInterfaces();
            Class controlInterface=null;
            for(Class interfaceclass:interfaces){
                if(ClassUtils.hasAnnotation(interfaceclass,MsgService.class)){
                    MsgService msgService=(MsgService)interfaceclass.getAnnotation(MsgService.class);
                    msgTouchMethodDispatcher.addCluster(msgService.value());
                    controlInterface=interfaceclass;
                    break;
                }
            }
            if(null!=controlInterface) {
                List<Method> rpcMethods = ClassUtils.findMethodsByAnnotation(controlInterface, MsgMethod.class);
                if (null != rpcMethods) {
                    MsgService msgService=(MsgService)controlInterface.getAnnotation(MsgService.class);
                    String serviceName=msgService.value();
                    for (Method method : rpcMethods) {
                        MsgMethod ma = method.getAnnotation(MsgMethod.class);
                        String cmd = ma.value();
                        if(serviceName!=null&&!"".equals(serviceName)){
                            cmd=serviceName+"/"+cmd;
                        }

                        Class[] paramTypes = method.getParameterTypes();
                        List<String> classNames = new ArrayList<String>(paramTypes.length);
                        for (Class paramType : paramTypes) {
                            classNames.add(paramType.getName());
                        }
                        MsgTouchMethodInvoker invoker = new MsgTouchMethodInvoker(method, controlClass.getClass());
                        msgTouchMethodDispatcher.addMethod(cmd, invoker);
                        logger.info("{} RpcService register method : {}=>{}.{}()", this, cmd, controlClass.getClass().getName(), method.getName());
                    }
                }
            }
        }

        return msgTouchMethodDispatcher;
    }



}
