package com.msgtouch.framework.utils;

import com.alibaba.fastjson.JSON;
import com.google.protobuf.AbstractMessage;
import com.google.protobuf.Message;
import com.msgtouch.framework.socket.packet.MsgBytePacket;
import com.msgtouch.framework.socket.packet.MsgType;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dean on 2016/11/25.
 */
public class CodecUtils {
    private static Map<String,ProtobufMapper> protobufMapperCache=new HashMap<String,ProtobufMapper>();
    private static class ProtobufMapper{
        String builderClassName;
        String protoClassName;
    }

    public static <T> T decode(MsgType msgType, Class<T> clazz, MsgBytePacket msgBytePacket) throws Exception{
        switch (msgType) {
            case JSON:
                return (T)decodeValue(clazz,msgBytePacket.readString());
            case ProtoBuf:
                Method newBuilderMethod=Class.forName(getProtoClassName(clazz)).getMethod("newBuilder");
                Message.Builder builder=(Message.Builder)newBuilderMethod.invoke(null,null);
                return (T)msgBytePacket.readProtoBuf(builder);
            default:
                throw new IllegalArgumentException("Unsupport msgType "+msgType);
        }
    }

    public static String encode(MsgType msgType, Class clazz, Object value) throws Exception{
        switch (msgType) {
            case JSON:
                return encodeValue(clazz,value);
            default:
                throw new IllegalArgumentException("Unsupport msgType "+msgType);
        }
    }

    public static String getProtoClassName(Class clazz){
        String builderClassName=null;
        boolean firstProto=true;
        String protoClassName=null;
        String className=clazz.getName();
        if(protobufMapperCache.containsKey(className)){
            builderClassName= protobufMapperCache.get(className).builderClassName;
            protoClassName=protobufMapperCache.get(className).protoClassName;
            firstProto=false;
        }else{
            builderClassName=className;
            if(AbstractMessage.Builder.class.isAssignableFrom(clazz)) {
                int lastIndex = builderClassName.lastIndexOf("Builder");
                protoClassName = builderClassName.substring(0, lastIndex-1);
                firstProto=true;
            }else{
                throw new UnsupportedOperationException("Unsupported this protobuf:" + className);
            }
        }
        if(firstProto){
            ProtobufMapper mapper=new ProtobufMapper();
            mapper.builderClassName=builderClassName;
            mapper.protoClassName=protoClassName;
            protobufMapperCache.put(className, mapper);
        }
        return protoClassName;
    }

    private static String encodeValue(Class c,Object value){
        String type=c.getName();
        if (type.equals("java.lang.String")) {
            return value.toString();
        } else if (type.equals("int")||type.equals("java.lang.Integer")) {
            return value+"";
        } else if (type.equals("long")||type.equals("java.lang.Long")) {
            return value+"";
        } else if (type.equals("boolean") || type.equals("java.lang.Boolean")) {
            return value+"";
        } else if (type.equals("double")||type.equals("java.lang.Double")) {
            return value+"";
        } else if (type.equals("float") || type.equals("java.lang.Float")) {
            return value+"";
        }else if(type.equals("char") || type.equals("java.lang.Character")){
            return value+"";
        }else if(type.equals("byte") || type.equals("java.lang.Byte")){
            return value+"";
        }else if(type.equals("short") || type.equals("java.lang.Short")){
            return value+"";
        }else{
            return JSON.toJSONString(value);
        }
    }


    private static Object decodeValue(Class c,String value){
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
        }else if(type.equals("char") || type.equals("java.lang.Character")){
            return  value.toCharArray()[0];
        }else if(type.equals("byte") || type.equals("java.lang.Byte")){
            return  Byte.parseByte(value);
        }else if(type.equals("short") || type.equals("java.lang.Short")){
            return  Short.parseShort(value);
        }else{
            return JSON.parseObject(value,c);
        }
    }



}
