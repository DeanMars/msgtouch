package com.msgtouch.broker.task;

import com.ecwid.consul.v1.health.model.HealthService;
import com.msgtouch.framework.cluster.TouchCluster;
import com.msgtouch.framework.registry.ConsulEngine;
import com.msgtouch.framework.context.Constraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Dean on 2017/1/4.
 */
@Component
@Configurable
@EnableScheduling
public class RouteTask {

    @Autowired
    private RouteHandler routeHandler;

    //每隔10秒执行一次
    @Scheduled(cron = "*/10 * *  * * * ")
    public void exec(){
        List<HealthService> serviceList=ConsulEngine.getInstance().getHealthService(Constraint.MSGTOUCH_TOUCHER);
        TouchCluster touchCluster=ConsulEngine.getInstance().getTouchCluster(Constraint.MSGTOUCH_TOUCHER);
        routeHandler.handler(serviceList,touchCluster);
    }


}
