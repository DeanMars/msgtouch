package com.msgtouch.broker.route;

import com.msgtouch.framework.consul.ConsulEngine;
import com.msgtouch.framework.context.Constraint;
import com.msgtouch.framework.settings.SettingsBuilder;
import com.orbitz.consul.Consul;
import com.orbitz.consul.async.ConsulResponseCallback;
import com.orbitz.consul.cache.ConsulCache;
import com.orbitz.consul.cache.ServiceHealthCache;
import com.orbitz.consul.cache.ServiceHealthKey;
import com.orbitz.consul.model.ConsulResponse;
import com.orbitz.consul.model.health.Service;
import com.orbitz.consul.model.health.ServiceHealth;
import com.orbitz.consul.option.QueryOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Dean on 2016/12/23.
 */
public class RouteManager {
    private static String root;
    private static Logger logger= LoggerFactory.getLogger(RouteManager.class);
    private static Consul consul;
    private static RouteManager routeManager;


    public static RouteManager getInstance(){
        if(null==routeManager){
            synchronized (RouteManager.class){
                if(null==routeManager){
                    routeManager=new RouteManager();
                }
            }
        }
        return routeManager;
    }

    public void init(ApplicationContext applicationContext){
        ConsulEngine.getInstance().bind(applicationContext);
        List<String> clusterList=new ArrayList<String>();
        clusterList.add(SettingsBuilder.buildContextSetting().APP_NAME);
        consul=ConsulEngine.getInstance().getConsul();
        ConsulEngine.getInstance().registeService(clusterList);
        root=applicationContext.getEnvironment().getProperty("consul.keyValueRoot");

    }

    public void watch(){
        watch(Constraint.MSGTOUCH_TOUCHER,root);
    }

    public void watch(String serviceName,String kvRoot){
        startServiceWatch(serviceName);
    }

    private void startServiceWatch(final String serviceName){
        ServiceHealthCache svHealth = ServiceHealthCache.newCache(consul.healthClient(), serviceName);
        svHealth.addListener(new ConsulCache.Listener<ServiceHealthKey, ServiceHealth>() {
            @Override
            public void notify(Map<ServiceHealthKey, ServiceHealth> newValues) {
                // do Something with updated server map
                for(Map.Entry<ServiceHealthKey, ServiceHealth> entry:newValues.entrySet()){
                    Service service=entry.getValue().getService();
                    String id=service.getId();
                    String address=service.getAddress();
                    int port=service.getPort();
                    logger.info("startServiceWatch entry key={},value={}",entry.getKey(),entry.getValue().getNode().toString());
                }
            }
        });
        try {
            svHealth.start();
        } catch (Exception e) {
            logger.info("startServiceWatch notify Exception e={}",e.getMessage());
        }



        /*consul.healthClient().getHealthyServiceInstances(serviceName, QueryOptions.blockSeconds(10, new BigInteger("0")).build(), new ConsulResponseCallback<List<ServiceHealth>>() {
            AtomicReference<BigInteger> index = new AtomicReference<BigInteger>(null);
            @Override
            public void onComplete(ConsulResponse<List<ServiceHealth>> consulResponse) {
                Iterator it=consulResponse.getResponse().iterator();
                while (it.hasNext()){
                    ServiceHealth serviceHealth=(ServiceHealth)it.next();
                    logger.info("getHealthyServiceInstances ServiceHealth ={}",serviceHealth.toString());
                }
                index.set(consulResponse.getIndex());
                keepWatch();
            }

            void keepWatch() {
                consul.healthClient().getHealthyServiceInstances(serviceName, QueryOptions.blockSeconds(10, index.get()).build(),this);
            }

            @Override
            public void onFailure(Throwable throwable) {
                logger.info("getHealthyServiceInstances ServiceHealth Exception e={}",throwable.getMessage());
                keepWatch();
            }
        });*/
    }


}
