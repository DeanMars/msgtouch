package com.msgtouch.framework.socket.dispatcher;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.msgtouch.framework.annotation.MsgEntity;
import com.msgtouch.framework.annotation.MsgParamter;
import com.msgtouch.framework.socket.packet.MsgPacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * Created by Dean on 2016/9/8.
 */
public class MsgTouchMethodDispatcher {
    private static Logger logger= LoggerFactory.getLogger(MsgTouchMethodDispatcher.class);

    private Map<String,MsgTouchMethodInvoker> methodInvokerMap =new HashMap<String,MsgTouchMethodInvoker>();
    private List<String> servicelist=new ArrayList<String>();

    public MsgTouchMethodDispatcher(){}

    public void addMethod(String cmd,MsgTouchMethodInvoker invoker){
        if(methodInvokerMap.containsKey(cmd)){
            logger.warn("RpcService method has exists:cmd={}",cmd);
            return;
        }
        methodInvokerMap.put(cmd, invoker);
    }

    public void dispatcher(ChannelHandlerContext ctx, MsgPacket bilingPacket){
        String cmd=bilingPacket.getCmd();
        MsgTouchMethodInvoker bilingMethodInvoker=methodInvokerMap.get(cmd);
        if(null==bilingMethodInvoker){
            throw new RuntimeException("MsgTouchMethodDispatcher method cmd="+cmd+" not found!");
        }
        String params = bilingPacket.getParams();

        if(null!=params) {
            Class[] parameterTypes = bilingMethodInvoker.getMethod().getParameterTypes();
            Annotation [][] annotations=bilingMethodInvoker.getMethod().getParameterAnnotations();
            Object []obj=new Object[parameterTypes.length];
            for( int i=0;i<parameterTypes.length;i++){
                Class c=parameterTypes[i];
                if(c.getName().equals(ChannelHandlerContext.class.getName()) ){
                    obj[i]=ctx;
                    continue;
                }
                if(c.getName().equals(MsgPacket.class.getName()) ){
                    obj[i]=bilingPacket;
                    continue;
                }
                Annotation [] parmaAnnotation=annotations[i];
                for(Annotation annotation:parmaAnnotation){
                    if(annotation instanceof MsgParamter){
                        MsgParamter bilingParamter=(MsgParamter)annotation;
                        String value=bilingParamter.value();
                        String defaultValue=bilingParamter.defaultValue();
                        JSONObject jSONObject=JSON.parseObject(params);
                        if(null!=jSONObject){
                            obj[i]=getValue(c,jSONObject.getString(value),defaultValue);
                        }
                    }else if(annotation instanceof MsgEntity){
                        obj[i]=JSON.parseObject(params,c);
                    }
                }
            }
            try {
                Object ret=bilingMethodInvoker.invoke(obj);
                bilingPacket.setParams(JSON.toJSONString(ret));
                Channel channel=ctx.channel();
                if(channel.isActive()) {
                    channel.writeAndFlush(bilingPacket);
                }else{
                    logger.error("channel is not active:packet = {}",bilingPacket);
                }
            } catch (Exception e) {
                logger.info("MsgTouchMethodDispatcher invoke method exception ！！");
                e.printStackTrace();
            }

        }

    }

    private Object getValue(Class c,String value,String defaultValue){
        if(null==value||"".equals(value)||"null".equals(value)){
            return getValue(c,defaultValue);
        }else{
            return getValue(c,value);
        }
    }

    private Object getValue(Class c,String value){
        String type=c.getName();
        if (type.equals("java.lang.String")) {
            return value.toString();
        } else if (type.equals("int")||type.equals("java.lang.Integer")) {
            return Integer.parseInt(value);
        } else if (type.equals("long")||type.equals("java.lang.Long")) {
            return Long.parseLong(value);
        } else if (type.equals("boolean") || type.equals("java.lang.Boolean")) {
            if("true".equals(value)){
                return true;
            }
            return false;
        } else if (type.equals("double")||type.equals("java.lang.Double")) {
            return Double.parseDouble(value);
        } else if (type.equals("float") || type.equals("java.lang.Float")) {
            return  Float.parseFloat(value);
        }
        return null;
    }


    public Set<String> getCmds(){
        return methodInvokerMap.keySet();
    }

    public void addServiceClass(String service){
        if(!servicelist.contains(service)){
            servicelist.add(service);
        }
    }

    public List<String> getServicelist() {
        return servicelist;
    }
}
