package com.msgtouch.broker.service;

import com.msgtouch.common.service.LoginService;
import com.msgtouch.framework.annotation.RpcService;
import org.springframework.stereotype.Service;

/**
 * Created by Dean on 2016/11/21.
 */
@Service
public class TestService {

    @RpcService
    private LoginService msgService;


    public String  test(){
        return msgService.getMsg();
    }


}
