package com.msgtouch.framework.consul;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Optional;
import com.google.common.net.HostAndPort;
import com.msgtouch.framework.cluster.TouchApp;
import com.msgtouch.framework.cluster.TouchCluster;
import com.msgtouch.framework.cluster.TouchService;
import com.msgtouch.framework.settings.ConsulSetting;
import com.msgtouch.framework.settings.SettingsBuilder;
import com.msgtouch.framework.socket.dispatcher.MethodDispatcher;
import com.orbitz.consul.AgentClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.HealthClient;
import com.orbitz.consul.KeyValueClient;
import com.orbitz.consul.cache.ConsulCache;
import com.orbitz.consul.cache.ServiceHealthCache;
import com.orbitz.consul.cache.ServiceHealthKey;
import com.orbitz.consul.model.agent.ImmutableRegCheck;
import com.orbitz.consul.model.agent.ImmutableRegistration;
import com.orbitz.consul.model.health.ServiceHealth;
import com.orbitz.consul.model.kv.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Created by Dean on 2016/11/17.
 */
public class ConsulEngine {
    private static final ConsulEngine consulEngine=new ConsulEngine();
    private  Logger logger= LoggerFactory.getLogger(ConsulEngine.class);
    private  Consul consul=null;
    private ConsulSetting setting=null;
    private ConsulEngine(){

    }

    public static ConsulEngine getInstance(){
        return consulEngine;
    }

    public void bind(ApplicationContext applicationContext){
        this.setting = SettingsBuilder.getConsulSetting();
        if(null==consul) {
            consul = Consul.builder().withHostAndPort(HostAndPort.fromString(setting.consulHostAndPort)).build();
        }
    }

    /**
     * 服务注册
     *
     */
    public void registeService(MethodDispatcher msgTouchMethodDispatcher){
        AgentClient agentClient=consul.agentClient();
        List<String> list=msgTouchMethodDispatcher.getClusterlist();
        for(String clusterName:list){
            ImmutableRegCheck immutableCheck=ImmutableRegCheck.builder().http(setting.healthUrl)
                    .interval(setting.healthIntervalSecond+"s").build();
            ImmutableRegistration.Builder builder=ImmutableRegistration.builder();
            String id=getServiceId(clusterName);
            builder.id(id).name(clusterName).addTags("")
                    .address(setting.ipAddress).port(setting.port).addChecks(immutableCheck);
            agentClient.register(builder.build());
        }
    }

    public void findServices(List<String> serviceList){
        HealthClient healthClient=consul.healthClient();
        for(String serviceName:serviceList){



            /*ConsulResponse<List<ServiceHealth>> consulResponse=healthClient.getHealthyServiceInstances(serviceName,
                    QueryOptions.blockSeconds(10,new BigInteger("0")).build());

            List<ServiceHealth> list=consulResponse.getResponse();
            for(ServiceHealth serviceHealth:list){
                logger.info(serviceHealth.toString());
            }*/
            ServiceHealthCache svHealth = ServiceHealthCache.newCache(healthClient, serviceName);

            svHealth.addListener(new ConsulCache.Listener<ServiceHealthKey, ServiceHealth>(){
                                     @Override
                                     public void notify(Map<ServiceHealthKey, ServiceHealth> map) {
                                            logger.info(map.toString());
                                     }
                                 }
            );
            try {
                svHealth.start();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }



    }


    /**
     * 服务获取
     */
    public void serviceGet(String serviceName) {
        HealthClient client = consul.healthClient();
        //获取所有正常的服务（健康检测通过的）
        Iterator<ServiceHealth> it=client.getHealthyServiceInstances(serviceName).getResponse().iterator();
        while (it.hasNext()){
            logger.info(it.next().toString());
        }

    }


    private <T> T getJsonObject(Class<T> clazz,String key){
        KeyValueClient keyValueClient=consul.keyValueClient();
        Optional<String> value= keyValueClient.getValueAsString(key);
        if(value.isPresent()){
            return JSON.parseObject(value.get(),clazz);
        }
        return null;
    }

    private void setJsonObject(String key,Object object){
        KeyValueClient keyValueClient=consul.keyValueClient();
        keyValueClient.putValue(key,JSON.toJSONString(object));
    }



    public void loginApp(long uid,String gameId){
        String appName=SettingsBuilder.buildContextSetting().APP_NAME;
        TouchCluster touchCluster=getJsonObject(TouchCluster.class,appName);
        if(null==touchCluster){
            touchCluster=new TouchCluster();
            touchCluster.setAppName(appName);
        }
        List<TouchService> serviceList=touchCluster.getServices();
        TouchService selfService=null;
        for(TouchService touchService:serviceList){
            if(touchService.getHost().equals(setting.ipAddress)&&touchService.getPort()==setting.port){
                selfService=touchService;
                break;
            }
        }
        if(selfService==null){
            selfService=new TouchService();
            selfService.setHost(setting.ipAddress);
            selfService.setPort(setting.port);
            touchCluster.addService(selfService);
        }
        List<TouchApp> appList=selfService.getAppList();
        TouchApp touchApp=new TouchApp();
        touchApp.setUid(uid);
        touchApp.setGameId(gameId);
        if(!appList.contains(touchApp)) {
            appList.add(touchApp);
        }
        setJsonObject(appName,touchCluster);
    }

    public void loginOutApp(long uid,String gameId){
        String appName=SettingsBuilder.buildContextSetting().APP_NAME;
        TouchCluster touchCluster=getJsonObject(TouchCluster.class,appName);
        if(null!=touchCluster) {
            List<TouchService> serviceList = touchCluster.getServices();
            TouchService selfService = null;
            for (TouchService touchService : serviceList) {
                if (touchService.getHost().equals(setting.ipAddress) && touchService.getPort() == setting.port) {
                    selfService = touchService;
                    break;
                }
            }
            if (selfService != null) {
                List<TouchApp> appList = selfService.getAppList();
                for (TouchApp touchApp : appList) {
                    if (touchApp.getGameId().equals(gameId) && touchApp.getUid() == uid) {
                        appList.remove(touchApp);
                        break;
                    }
                }
            }
            setJsonObject(appName, touchCluster);
        }
    }



    private String getServiceId(String appName){
        return appName+"_"+setting.ipAddress+":"+setting.port;
    }
}
