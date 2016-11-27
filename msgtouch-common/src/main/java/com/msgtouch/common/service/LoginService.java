package com.msgtouch.common.service;

import com.msgtouch.framework.annotation.MsgMethod;
import com.msgtouch.framework.annotation.MsgService;

/**
 * Created by Dean on 2016/10/12.
 */
@MsgService("loginService")
public interface LoginService {
    @MsgMethod("login")
    String login(String dtr);


    @MsgMethod("login")
    String test(boolean arg1,String arg2,char arg3,byte arg4,short arg5,int arg6,float arg7,double arg8,long arg9);


}
