package com.msgtouch.framework.utils;

import com.alibaba.fastjson.JSON;
import com.msgtouch.framework.socket.packet.MsgType;

/**
 * Created by Dean on 2016/11/25.
 */
public class CodecUtils {

    public static Object decode(MsgType msgType, Class clazz, String value){
        switch (msgType) {
            case JSON:
                return decodeValue(clazz,value);
            default:
                throw new IllegalArgumentException("Unsupport msgType "+msgType);
        }
    }

    public static String encode(MsgType msgType, Class clazz, Object value){
        switch (msgType) {
            case JSON:
                return encodeValue(clazz,value);
            default:
                throw new IllegalArgumentException("Unsupport msgType "+msgType);
        }
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
