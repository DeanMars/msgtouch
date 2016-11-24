package com.msgtouch.common.service;

import com.msgtouch.framework.annotation.MsgMethod;
import com.msgtouch.framework.annotation.MsgService;

/**
 * Created by Dean on 2016/10/12.
 */
@MsgService("loginService")
public interface LoginService {
    @MsgMethod("login")
    String login();

}
