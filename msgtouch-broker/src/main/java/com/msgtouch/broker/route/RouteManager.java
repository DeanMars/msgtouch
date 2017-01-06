package com.msgtouch.broker.route;

import com.msgtouch.framework.registry.ConsulEngine;
import com.msgtouch.framework.setting.SettingsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Dean on 2016/12/23.
 */
public class RouteManager {
   // private static String root;
    private static Logger logger= LoggerFactory.getLogger(RouteManager.class);
   // private static ConsulClient consulClient;
    private static RouteManager routeManager;
    private static List<RouteTarget> routeTargetList=null;
    private static ConcurrentHashMap<String,RouteTarget> routeAppMap=null;


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
        //consulClient=ConsulEngine.getInstance().getConsulClient();
        ConsulEngine.getInstance().registeService(clusterList);
       //root=applicationContext.getEnvironment().getProperty("consul.keyValueRoot");

    }


    public void refreshRoute(List<RouteTarget> list,Map<String,RouteTarget> appMap){
        this.routeTargetList=list;
        this.routeAppMap=new ConcurrentHashMap<String, RouteTarget>(appMap);
    }

    public List<Router> routeByGameId(String gameId){
        List<RouteTarget> result=new ArrayList<RouteTarget>();
        if(null!=gameId&&!"".equals(gameId)){
            if(null!=routeAppMap) {
                for(Map.Entry<String,RouteTarget> entry:routeAppMap.entrySet()){
                    String key=entry.getKey();
                    String []args=key.split("_");
                    if(null!=args&&args.length>1){
                        String targetGameId=args[1];
                        if(targetGameId.equals(gameId)){
                            RouteTarget routeTarget=entry.getValue();
                            RouteTarget target=cloneRouteTarget(routeTarget);
                            result.add(target);
                        }
                    }
                }
            }
        }
        return routeTarget2Router(result);
    }

    public List<Router> routeByUid(String uid,boolean singleApp){
        List<RouteTarget> result=new ArrayList<RouteTarget>();
        if(null!=uid&&!"".equals(uid)){
            if(null!=routeAppMap) {
                for(Map.Entry<String,RouteTarget> entry:routeAppMap.entrySet()){
                    String key=entry.getKey();
                    String []args=key.split("_");
                    if(null!=args&&args.length>=1){
                        String targetUid=args[0];
                        if(targetUid.equals(uid)){
                            RouteTarget routeTarget=entry.getValue();
                            RouteTarget target=cloneRouteTarget(routeTarget);
                            result.add(target);
                            if(singleApp){
                                break;
                            }
                        }
                    }
                }
            }
        }
        return routeTarget2Router(result);
    }

    public List<Router> routeByUidAndGameId(String uid,String gameId){
        List<RouteTarget> result=new ArrayList<RouteTarget>();
        if(null!=uid&&!"".equals(uid)){
            if(null!=routeAppMap) {
                for(Map.Entry<String,RouteTarget> entry:routeAppMap.entrySet()){
                    String key=entry.getKey();
                    String []args=key.split("_");
                    if(null!=args&&args.length>=1){
                        String targetUid=args[0];
                        String targetGameid=args[1];
                        if(targetUid.equals(uid)&&targetGameid.equals(gameId)){
                            RouteTarget routeTarget=entry.getValue();
                            RouteTarget target=cloneRouteTarget(routeTarget);
                            result.add(target);
                            break;
                        }
                    }
                }
            }
        }
        return routeTarget2Router(result);
    }


    private List<Router> routeTarget2Router(List<RouteTarget> routeTargets){
        List<Router> result=new ArrayList<Router>();
        if(null!=routeTargets&&routeTargets.size()>0) {
            Map<String, List<RouteTarget>> dataMap = new HashMap<String, List<RouteTarget>>();
            for (RouteTarget routeTarget : routeTargets) {
                String key = routeTarget.getAppName() + "_" + routeTarget.getAddress() + "_" + routeTarget.getPort();
                List<RouteTarget> list = dataMap.get(key);
                if (null == list) {
                    list = new ArrayList<RouteTarget>();
                }
                list.add(routeTarget);
                dataMap.put(key, list);
            }

            for (Map.Entry<String, List<RouteTarget>> entry : dataMap.entrySet()) {
                String key = entry.getKey();
                String[] args = key.split("_");

                Router router = new Router();
                router.setAppName(args[0]);
                router.setAddress(args[1]);
                router.setPort(Integer.parseInt(args[2]));
                List<RouteTarget> targets = entry.getValue();
                for (RouteTarget routeTarget : targets) {
                    RouteTag routeTag = new RouteTag();
                    routeTag.setGameId(routeTarget.getGameId());
                    routeTag.setUid(routeTarget.getUid());
                    router.addRouteTag(routeTag);
                }
                result.add(router);
            }
        }
        return result;
    }

    private RouteTarget cloneRouteTarget(RouteTarget routeTarget){
        RouteTarget result=null;
        if(null!=routeTarget){
            try {
                result=(RouteTarget)routeTarget.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

}
