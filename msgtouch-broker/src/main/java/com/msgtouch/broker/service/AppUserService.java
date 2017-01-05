package com.msgtouch.broker.service;

import com.msgtouch.broker.route.RouteManager;
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

}
