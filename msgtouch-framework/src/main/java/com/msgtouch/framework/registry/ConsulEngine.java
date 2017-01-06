package com.msgtouch.framework.registry;

import com.alibaba.fastjson.JSON;
import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.agent.model.NewService;
import com.ecwid.consul.v1.health.model.HealthService;
import com.ecwid.consul.v1.kv.model.GetValue;
import com.msgtouch.framework.cluster.TouchApp;
import com.msgtouch.framework.cluster.TouchCluster;
import com.msgtouch.framework.cluster.TouchService;
import com.msgtouch.framework.setting.ConsulSetting;
import com.msgtouch.framework.setting.SettingsBuilder;
import com.msgtouch.network.registry.RegistryEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dean on 2017/1/3.
 */
public class ConsulEngine implements RegistryEngine {
    private Logger logger= LoggerFactory.getLogger(ConsulEngine.class);
    private ConsulSetting setting=null;
    private ConsulClient consulClient=null;
    private static ConsulEngine consulEngine=null;
    private ConsulEngine(){}

    public  static ConsulEngine getInstance(){
        if(null==consulEngine){
            synchronized (ConsulEngine.class){
                if(null==consulEngine){
                    consulEngine=new ConsulEngine();
                }
            }
        }
        return consulEngine;
    }



    public void bind(ApplicationContext applicationContext){
        this.setting = SettingsBuilder.getConsulSetting();
        consulClient=new ConsulClient(setting.consulHostAndPort);
    }

    private String getServiceId(String appName){
        return appName+"_"+setting.ipAddress+":"+setting.port;
    }
    /**
     * 服务注册
     *
     */
    public void registeService(List<String> clusterList){
        for(String clusterName:clusterList){
            String id=getServiceId(clusterName);
            NewService newService = new NewService();
            newService.setId(id);
            newService.setName(clusterName);
            List<String> tags=new ArrayList<String>();
            tags.add(clusterName);
            newService.setTags(tags);
            newService.setPort(setting.port);
            newService.setAddress(setting.ipAddress);

            NewService.Check serviceCheck = new NewService.Check();
            serviceCheck.setHttp(setting.healthUrl);
            serviceCheck.setInterval(setting.healthIntervalSecond+"s");
            newService.setCheck(serviceCheck);
            consulClient.agentServiceRegister(newService);

        }
    }

    /**
     * 服务获取
     */
    public List<HealthService> getHealthService(String serviceName) {
        Response<List<HealthService>> response=consulClient.getHealthServices(serviceName,true,null);
        List<HealthService> list=response.getValue();
        return list;
    }

    public TouchCluster getTouchCluster(String appName) {
        TouchCluster touchCluster = getJsonObject(TouchCluster.class, appName);
        return touchCluster;
    }


    public String getKValueSign(String key){
        String result=null;
        Response<GetValue> response=consulClient.getKVValue(key);
        GetValue getValue=response.getValue();
        if(null!=getValue){
            result=getValue.getValue().toString();
        }
        return result;
    }


    public <T> T getJsonObject(Class<T> clazz,String key){
        Response<GetValue> response=consulClient.getKVValue(key);
        GetValue getValue=response.getValue();
        if(null!=getValue){
            String value=getValue.getDecodedValue();
            if(null!=value){
                return JSON.parseObject(value,clazz);
            }
        }
        return null;
    }

    public void setJsonObject(String key,Object object){
        consulClient.setKVValue(key,JSON.toJSONString(object));
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

    public ConsulClient getConsulClient() {
        return consulClient;
    }
}
