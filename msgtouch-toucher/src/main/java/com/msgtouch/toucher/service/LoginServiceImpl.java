package com.msgtouch.toucher.service;

import com.msgtouch.common.service.LoginService;
import org.springframework.stereotype.Service;

/**
 * Created by Dean on 2016/10/9.
 */
@Service
public class LoginServiceImpl implements LoginService {

    @Override
    public String login(String str) {
        return "LoginServiceImpl getMsg success  ----" +str;
    }


}
