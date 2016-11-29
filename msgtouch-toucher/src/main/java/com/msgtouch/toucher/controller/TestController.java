package com.msgtouch.toucher.controller;

import com.msgtouch.toucher.service.PushServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Dean on 2016/11/21.
 */
@RestController
public class TestController {
    @Autowired
    private PushServiceImpl pushServiceImpl;
    @RequestMapping("pushAll")
    public String  pushAll(){
        pushServiceImpl.pushAll();
        return "";
    }

}
