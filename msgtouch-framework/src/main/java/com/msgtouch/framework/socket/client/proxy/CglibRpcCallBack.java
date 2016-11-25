package com.msgtouch.framework.socket.client.proxy;

import com.msgtouch.framework.annotation.MsgMethod;
import com.msgtouch.framework.annotation.MsgService;
import com.msgtouch.framework.socket.client.MsgTouchClientApi;
import com.msgtouch.framework.utils.ClassUtils;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Created by Dean on 2016/11/23.
 */
public class CglibRpcCallBack implements MethodInterceptor {
    MsgTouchClientApi rpcClientApi=MsgTouchClientApi.getInstance();
    private boolean sync=true;
    public CglibRpcCallBack(boolean sync){
        this.sync=sync;
    }

    @Override
    public Object intercept(Object o, Method method, Object[] params, MethodProxy methodProxy) throws Throwable {
        Class[] interfaces=o.getClass().getInterfaces();
        Class controlInterface=null;
        for(Class i:interfaces){
            if(ClassUtils.hasAnnotation(i,MsgService.class)){
                controlInterface=i;
                break;
            }
        }
        if(controlInterface==null){
            throw new IllegalArgumentException("Interface has no @MsgService annotation:class = "+o.getClass());
        }
        MsgService rpcControl = (MsgService) controlInterface.getAnnotation(MsgService.class);
        String clusterName= rpcControl.value();
        MsgMethod cmdMethod=method.getAnnotation(MsgMethod.class);
        if(cmdMethod==null){
            return  null;
        }
        String cmd=rpcControl.value()+"/"+method.getAnnotation(MsgMethod.class).value();
        Class returnType=method.getReturnType();
        if(sync){
            return rpcClientApi.syncRpcCall(clusterName,cmd,returnType,params);
        }else{
           // rpcClientApi.asyncRpcCall(clusterName,cmd,params);
            return null;
        }
    }
}
