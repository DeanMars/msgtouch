package com.msgtouch.framework.socket.dispatcher;

import com.msgtouch.framework.context.SpringBeanAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import java.lang.reflect.Method;

/**
 * Created by Dean on 2016/9/8.
 */
public class MsgTouchMethodInvoker {
    private Method method;
    private Class clazz;
    private static Logger log= LoggerFactory.getLogger(MsgTouchMethodInvoker.class);
    public MsgTouchMethodInvoker(Method method, Class clazz){
        this.method=method;
        this.clazz=clazz;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }
    public Object invoke(Object[] params) throws Exception{
        Object target=null;
        try{
            target= SpringBeanAccess.getInstances().getBean(clazz);
        }catch (NoSuchBeanDefinitionException e){
            log.error("Spring context can not find this bean:{},default call class.newInstance() ",clazz.getName());
        }
        return method.invoke(target,params);
    }

}
