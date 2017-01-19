package com.msgtouch.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;


/**
 * Created by Jason on 2015/8/11.
 */
public class JsonUtil {


    /**
     * 对象转Json，忽略transient的属性
     *
     * @param object 对象
     * @return
     */
    public static String toIgoneJson(Object object) {
        return JSON.toJSONString(object);
    }

    /**
     * 对象转json, 不忽略空字段
     * @param object
     * @return
     */
    public static String toIgoneJsonWithNullValue(Object object) {
        return JSON.toJSONString(object, SerializerFeature.WriteMapNullValue);
    }

    /**
     * 对象转Json，不忽略transient的属性
     *
     * @param object 对象
     * @return
     */
    public static String toJson(Object object) {
        SerializerFeature skipTransient = SerializerFeature.SkipTransientField;
        SerializeWriter writer = new SerializeWriter();
        JSONSerializer serializable = new JSONSerializer(writer);
        serializable.config(skipTransient, false);
        serializable.write(object);
        return writer.toString();

    }

    public static void main(String[] args) {
        Set<Integer> set = new HashSet<Integer>();
        while (set.size()<120) {
            Random random = new Random();
            int i = random.nextInt(999999)%(999999-100001) + 100000;
            set.add(i);
        }

        for (Integer i : set) {
            long id = i;
            System.out.println(i);
        }
    }

}
