package com.msgtouch.broker.controller;

import com.msgtouch.broker.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Dean on 2016/11/21.
 */
@RestController
public class PushController {
    @Autowired
    private AppUserService appUserService;



    @RequestMapping("pushMsg")
    public String  pushMsg(@RequestParam(value = "msg",required = true)String msg,
                           @RequestParam(value = "uid",required = false,defaultValue = "0")long uid,
                           @RequestParam(value = "gameId",required = false,defaultValue = "")String gameId
                           ){
        if("".equals(gameId)&&uid==0){
            return "{\"code\":1,\"msg\":\"参数错误\"}";
        }
        if(uid==0&&!"".equals(gameId)){
            appUserService.pushSimpleMsgByGameId(gameId,msg);
        }else if(uid!=0&&"".equals(gameId)){
            appUserService.pushSimpleMsgByUid(uid,msg);
        }else{
            appUserService.pushSimpleMsg(uid,gameId,msg);
        }

        return "{\"code\":0,\"msg\":\"success\"}";
    }


}
