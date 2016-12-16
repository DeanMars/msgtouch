package com.msgtouch.common.service;

import com.msgtouch.common.context.Constraint;
import com.msgtouch.framework.annotation.MsgMethod;
import com.msgtouch.framework.annotation.MsgService;

/**
 * Created by Dean on 2016/10/12.
 */
@MsgService(Constraint.MSGTOUCH_TOUCHER)
public interface TestService {
    @MsgMethod("test1")
    String test(String dtr);


    @MsgMethod("test")
    String test(boolean arg1,String arg2,char arg3,byte arg4,short arg5,int arg6,float arg7,double arg8,long arg9);


}
