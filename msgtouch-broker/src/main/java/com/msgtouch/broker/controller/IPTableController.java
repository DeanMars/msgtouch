package com.msgtouch.broker.controller;

import com.msgtouch.broker.route.RouteTarget;
import com.msgtouch.broker.service.AppUserService;
import com.msgtouch.common.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dean on 2016/11/21.
 */
@RestController
public class IPTableController {
    @Autowired
    private AppUserService appUserService;

    @RequestMapping("getHost")
    public String  pushMsg(@RequestParam(value = "uid",required = false,defaultValue = "0")long uid,
                           @RequestParam(value = "gameId",required = false,defaultValue = "")String gameId
                           ){
        if("".equals(gameId)&&uid==0){
            return "{\"code\":1,\"msg\":\"参数错误\"}";
        }
        RouteTarget routeTarget=appUserService.getHost(uid,gameId);
        String ip=null;
        int port=0;
        if(null!=routeTarget){
            ip=routeTarget.getAddress();
            port=routeTarget.getPort();
        }
        Map<String,Object> result=new HashMap<String,Object>();
        result.put("code",0);
        result.put("msg","success");

        Map<String,Object> data=new HashMap<String,Object>();
        data.put("ip",ip);
        data.put("port",port);

        result.put("data",data);

        return JsonUtil.toJson(result);
    }


}
