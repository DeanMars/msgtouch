package com.msgtouch.broker.task;

import com.ecwid.consul.v1.health.model.HealthService;
import com.msgtouch.framework.cluster.TouchCluster;
import com.msgtouch.framework.registry.ConsulEngine;
import com.msgtouch.network.context.Constraint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Dean on 2017/1/4.
 */
@Component
@Configurable
@EnableScheduling
public class RouteTask {
    private static Logger logger= LoggerFactory.getLogger(RouteTask.class);
    @Autowired
    private RouteHandler routeHandler;
    private static String sign;
    private static List<HealthService> lastServiceList=new ArrayList<HealthService>();
    //每隔10秒执行一次
    @Scheduled(cron = "*/10 * *  * * * ")
    public void exec(){
        List<HealthService> serviceList=ConsulEngine.getInstance().getHealthService(Constraint.MSGTOUCH_TOUCHER);
        boolean needRefreshCache=checkServiceChange(serviceList);
        if(!needRefreshCache){
            String signtemp=ConsulEngine.getInstance().getKValueSign(Constraint.MSGTOUCH_TOUCHER);
            if(signtemp!=null&&!signtemp.equals(sign)){
                needRefreshCache=true;
                RouteTask.sign = signtemp;
            }
        }
        logger.info("RouteTask exec needRefreshCache={},time={} ms",needRefreshCache,System.currentTimeMillis());
        if(needRefreshCache) {
            TouchCluster touchCluster = ConsulEngine.getInstance().getTouchCluster(Constraint.MSGTOUCH_TOUCHER);
            String handlerId=UUID.randomUUID().toString();
            logger.info("RouteTask start handler handlerId={},time={} ms",handlerId,System.currentTimeMillis());
            routeHandler.handler(serviceList, touchCluster,handlerId);
        }
    }

    private boolean checkServiceChange(List<HealthService> serviceList){
        if(serviceList.size()!=lastServiceList.size()){
            RouteTask.lastServiceList=serviceList;
            return true;
        }else{
            for(HealthService healthService:serviceList){
                HealthService.Service service=healthService.getService();
                String ip=service.getAddress();
                int port=service.getPort();
                boolean flag=false;
                for(HealthService lastHealthService:lastServiceList){
                    HealthService.Service lastService=lastHealthService.getService();
                    String lastIp=lastService.getAddress();
                    int lastPort=lastService.getPort();
                    if(lastIp.equals(ip)&&lastPort==port){
                        flag=true;
                        break;
                    }
                }
                if(!flag){
                    RouteTask.lastServiceList=serviceList;
                    return true;
                }
            }
        }
        return false;
    }

}
