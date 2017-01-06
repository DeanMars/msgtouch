package com.msgtouch.broker.task;

import com.ecwid.consul.v1.health.model.HealthService;
import com.msgtouch.broker.route.RouteManager;
import com.msgtouch.broker.route.RouteTarget;
import com.msgtouch.framework.cluster.TouchApp;
import com.msgtouch.framework.cluster.TouchCluster;
import com.msgtouch.framework.cluster.TouchService;
import com.msgtouch.network.context.Constraint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Dean on 2017/1/4.
 */
@Component
public class RouteHandler {
    private static Logger logger= LoggerFactory.getLogger(RouteHandler.class);

    private ExecutorService executorService=Executors.newSingleThreadExecutor();

    public void handler(List<HealthService> serviceList, TouchCluster touchCluster,String handlerId){
        executorService.submit(new Handler(serviceList,touchCluster,handlerId));
    }


    class Handler implements Runnable{
        private List<HealthService> serviceList;
        private TouchCluster touchCluster;
        private String handlerId;

        public Handler(List<HealthService> serviceList, TouchCluster touchCluster,String handlerId) {
            this.serviceList = serviceList;
            this.touchCluster = touchCluster;
            this.handlerId=handlerId;
        }

        @Override
        public void run() {
            long start=System.currentTimeMillis();
            logger.info("RouteHandler run handlerData start time={} ",start);
            Map<String,RouteTarget> appMap=new HashMap<String,RouteTarget>();
            List<RouteTarget> list=new ArrayList<RouteTarget>();
            for(HealthService healthService:serviceList){
                String address=healthService.getService().getAddress();
                int port=healthService.getService().getPort();

                RouteTarget target=new RouteTarget();
                target.setAppName(Constraint.MSGTOUCH_TOUCHER);
                target.setAddress(address);
                target.setPort(port);

                list.add(target);

                for(TouchService touchService:touchCluster.getServices()){
                    String touchServiceAddress=touchService.getHost();
                    int touchServicePort=touchService.getPort();

                    if(touchServiceAddress.equals(address)&&port==touchServicePort) {
                        for (TouchApp touchApp : touchService.getAppList()) {
                            String key = touchApp.getUid() + "_" + touchApp.getGameId();
                            RouteTarget userTarget=new RouteTarget();
                            userTarget.setAppName(Constraint.MSGTOUCH_TOUCHER);
                            userTarget.setAddress(address);
                            userTarget.setPort(port);
                            userTarget.setUid(touchApp.getUid());
                            userTarget.setGameId(touchApp.getGameId());
                            appMap.put(key, userTarget);
                        }
                        break;
                    }

                }
            }
            long end=System.currentTimeMillis();
            logger.info("RouteHandler run handlerData end time={} ",end);
            RouteManager.getInstance().refreshRoute(list,appMap);
            end=System.currentTimeMillis();
            logger.info("RouteHandler run refreshRoute end time={} ",end);
        }

    }


}
