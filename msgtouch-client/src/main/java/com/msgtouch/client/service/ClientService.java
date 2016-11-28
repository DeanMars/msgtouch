package com.msgtouch.client.service;

import com.msgtouch.common.service.LoginService;
import com.msgtouch.framework.annotation.RpcService;
import org.springframework.stereotype.Service;

/**
 * Created by Dean on 2016/11/28.
 */
@Service
public class ClientService {

    @RpcService
    private LoginService loginService;


    public String test(){
        return loginService.login("43543543534543");
    }

}
