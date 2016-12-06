package com.msgtouch.framework.consul;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Optional;
import com.google.common.net.HostAndPort;
import com.msgtouch.framework.cluster.TouchCluster;
import com.msgtouch.framework.cluster.TouchRoot;
import com.msgtouch.framework.cluster.TouchService;
import com.msgtouch.framework.context.Constraint;
import com.msgtouch.framework.settings.ConsulSetting;
import com.msgtouch.framework.settings.SettingsBuilder;
import com.msgtouch.framework.socket.dispatcher.JsonPacketMethodDispatcher;
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
import java.util.Set;


/**
 * Created by Dean on 2016/11/17.
 */
public class ConsulEngine {
    private static final ConsulEngine consulEngine=new ConsulEngine();
    private  Logger logger= LoggerFactory.getLogger(ConsulEngine.class);
    private  Consul consul=null;
    private JsonPacketMethodDispatcher msgTouchMethodDispatcher;
    private ConsulSetting setting=null;
    private TouchCluster touchCluster;
    private ConsulEngine(){

    }

    public static ConsulEngine getInstance(){
        return consulEngine;
    }

    public void bind(ApplicationContext applicationContext,JsonPacketMethodDispatcher msgTouchMethodDispatcher){
        this.msgTouchMethodDispatcher=msgTouchMethodDispatcher;
        this.setting = SettingsBuilder.getConsulSetting();
        if(null==consul) {
            consul = Consul.builder().withHostAndPort(HostAndPort.fromString(setting.consulHostAndPort)).build();
        }
        registeService();

    }

    /**
     * 服务注册
     *
     */
    public void registeService(){
        AgentClient agentClient=consul.agentClient();
        List<String> list=msgTouchMethodDispatcher.getClusterlist();
        for(String clusterName:list){
            ImmutableRegCheck immutableCheck=ImmutableRegCheck.builder().http(setting.healthUrl)
                    .interval(setting.healthIntervalSecond+"s").build();
            ImmutableRegistration.Builder builder=ImmutableRegistration.builder();
            builder.id(clusterName+"_"+setting.ipAddress+":"+setting.port).name(clusterName).addTags("")
                    .address(setting.ipAddress).port(setting.port).addChecks(immutableCheck);
            agentClient.register(builder.build());

        }

        //this.touchCluster=buildTouchCluster(msgTouchMethodDispatcher, setting);
        //registeProvider(touchCluster,msgTouchMethodDispatcher);

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

    public void registeProvider(TouchCluster touchCluster,JsonPacketMethodDispatcher msgTouchMethodDispatcher){
        KeyValueClient keyValueClient=consul.keyValueClient();
        TouchRoot root=getTouchServiceRoot(keyValueClient);
        for(String serviceClasssName:msgTouchMethodDispatcher.getClusterlist()){
            TouchService touchService=null;
            if(root.contailService(serviceClasssName)){
                touchService= root.getSerrviceByName(serviceClasssName);
                touchService.addProvider(touchCluster);
            }else{
                touchService=new TouchService();
                touchService.setName(serviceClasssName);
                touchService.addProvider(touchCluster);
            }
            root.addServices(touchService);
            logger.info("TouchRoot addServices serviceName={}",touchService.getName());
        }
        keyValueClient.putValue(Constraint.FRAMEWORK_SERVICE_KEY,JSON.toJSONString(root));

    }


    public void registeConsumer(List<String> services){
        KeyValueClient keyValueClient=consul.keyValueClient();
        TouchRoot root=getTouchServiceRoot(keyValueClient);
        for(String service:services){
            if(root.contailService(service)){
                TouchService touchService=root.getSerrviceByName(service);
                touchService.addConsumer(touchCluster);
            }else{
                throw new RuntimeException("MsgTouch registeConsumer Exception service "+service+"not exist!");
            }
        }

    }

    private TouchCluster buildTouchCluster(JsonPacketMethodDispatcher msgTouchMethodDispatcher, ConsulSetting setting){
        TouchCluster touchCluster=new TouchCluster();
        touchCluster.setIp(setting.ipAddress);
        touchCluster.setPort(setting.port);
       // touchCluster.setVersion(Bootstrap.getInstances().getContextSetting().APP_VERSION);
      //  touchCluster.setExt(Bootstrap.getInstances().getContextSetting().APP_EXT);
        Set<String> cmds=msgTouchMethodDispatcher.getCmds();
        touchCluster.setCmds(cmds);
        return touchCluster;
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


    private  TouchRoot  getTouchServiceRoot(KeyValueClient keyValueClient){
        TouchRoot root=null;
        Optional<Value> value= keyValueClient.getValue(Constraint.FRAMEWORK_SERVICE_KEY);
        if(!value.isPresent()){
            root=new TouchRoot();
        }else{
            root=JSON.parseObject(value.toString(),TouchRoot.class);
        }
        return root;
    }

}
