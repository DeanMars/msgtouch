package com.msgtouch.broker.utils;

import com.google.common.base.Optional;
import com.msgtouch.framework.Bootstrap;
import com.msgtouch.framework.consul.ConsulEngine;
import com.msgtouch.framework.settings.SettingsBuilder;
import com.orbitz.consul.Consul;
import com.orbitz.consul.KeyValueClient;
import com.orbitz.consul.async.ConsulResponseCallback;
import com.orbitz.consul.cache.ConsulCache;
import com.orbitz.consul.cache.KVCache;
import com.orbitz.consul.model.ConsulResponse;
import com.orbitz.consul.model.kv.Value;
import com.orbitz.consul.option.QueryOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Dean on 2016/12/23.
 */
public class RouteManager {

    private  static Logger logger= LoggerFactory.getLogger(RouteManager.class);

    public static void init(ApplicationContext applicationContext){
        //初始化上下文
        Bootstrap.getInstances().initContext(applicationContext);
        ConsulEngine.getInstance().bind(applicationContext);
        List<String> clusterList=new ArrayList<String>();
        clusterList.add(SettingsBuilder.buildContextSetting().APP_NAME);
        ConsulEngine.getInstance().registeService(clusterList);
        String root=applicationContext.getEnvironment().getProperty("consul.keyValueRoot");
        service(root);
    }

    private static void service(String root){
        Consul consul=ConsulEngine.getInstance().getConsul();

        KeyValueClient keyValueClient=consul.keyValueClient();
        keyValueClient.getValue(root, QueryOptions.blockSeconds(10,new BigInteger("0")).build(),new ConsulResponseCallback<Optional<Value>>(){
            @Override
            public void onComplete(ConsulResponse<Optional<Value>> consulResponse) {
                if (consulResponse.getResponse().isPresent()) {
                    Value v = consulResponse.getResponse().get();
                    logger.info("Value is: {}", v.getValue().get());
                }

            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });

        /*KeyValueClient keyValueClient=consul.keyValueClient();
        KVCache kVCache=KVCache.newCache(keyValueClient,root);
        kVCache.addListener(new ConsulCache.Listener<String, Value>() {
            @Override
            public void notify(Map<String, Value> map) {
                for(Map.Entry<String, Value> entry:map.entrySet()){
                    logger.info("service  KVCache notify  entry key={},value={}",entry.getKey(),entry.getValue().getValueAsString().get());
                }
            }
        });
        try {
            kVCache.start();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

}
