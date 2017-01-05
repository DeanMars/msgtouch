package com.msgtouch.toucher.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Dean on 2016/11/21.
 */
@RestController
public class HealthController {

    @RequestMapping("health")
    public String  health(){
        return "{\"code\":0,\"msg\":\"success\"}";
    }

}
