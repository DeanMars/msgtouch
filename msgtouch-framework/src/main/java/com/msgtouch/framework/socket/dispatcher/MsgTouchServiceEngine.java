package com.msgtouch.framework.socket.dispatcher;

import com.msgtouch.framework.context.SpringBeanAccess;
import com.msgtouch.framework.annotation.MsgMethod;
import com.msgtouch.framework.annotation.MsgService;
import com.msgtouch.framework.utils.ClassUtils;
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

    private static MsgTouchServiceEngine serviceEngine=null;
    private MsgTouchServiceEngine(){}

    public static MsgTouchServiceEngine getInstances(){
        if(null==serviceEngine){
            synchronized (MsgTouchServiceEngine.class){
                if(null==serviceEngine) {
                    serviceEngine = new MsgTouchServiceEngine();
                }
            }
        }
        return serviceEngine;
    }



    public MethodDispatcher loadService(MethodDispatcher msgTouchMethodDispatcher){
        String [] beanNames=SpringBeanAccess.getInstances().getApplicationContext().getBeanDefinitionNames();
        for(String beanName:beanNames){
            Object controlClass=SpringBeanAccess.getInstances().getApplicationContext().getBean(beanName);
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
