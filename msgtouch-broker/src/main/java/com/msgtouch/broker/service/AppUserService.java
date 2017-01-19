package com.msgtouch.broker.service;

import com.msgtouch.broker.route.RouteManager;
import com.msgtouch.broker.route.RouteTarget;
import com.msgtouch.broker.route.Router;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Dean on 2016/12/30.
 */
@Service
public class AppUserService {

    public void pushSimpleMsgByGameId(String gameId,String msg){
        List<Router> list=RouteManager.getInstance().routeByGameId(gameId);
        for(Router router:list){

        }

    }

    public void pushSimpleMsgByUid(long uid,String msg){
        List<Router> list=RouteManager.getInstance().routeByUid(uid+"",true);
        for(Router router:list){

        }

    }

    public void pushSimpleMsg(long uid,String gameId,String msg){
        List<Router> list=RouteManager.getInstance().routeByUidAndGameId(uid+"",gameId);
        for(Router router:list){

        }

    }


    public RouteTarget getHost(long uid,String gameId){
        return getHost();
    }

    public RouteTarget getHost(){
        List<RouteTarget> list=RouteManager.getInstance().getRouteTargetList();
        if(list.size()==0){
            return list.get(0);
        }else{
            int min=0;
            RouteTarget result=null;
            for(RouteTarget routeTarget:list){
                int size = routeTarget.getSize();
                if(size==0){
                    return routeTarget;
                }else{
                    if(min==0) {
                        min = size;
                        result=routeTarget;
                    }else{
                        if(size<min){
                            min = size;
                            result=routeTarget;
                        }
                    }
                }

            }
            return result;
        }
    }



}
