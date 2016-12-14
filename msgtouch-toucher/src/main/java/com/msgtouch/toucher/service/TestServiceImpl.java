package com.msgtouch.toucher.service;

import com.msgtouch.common.service.TestService;
import org.springframework.stereotype.Service;

/**
 * Created by Dean on 2016/10/9.
 */
@Service
public class TestServiceImpl implements TestService {

    @Override
    public String login(String str) {
        return "LoginServiceImpl getMsg success  ----" +str;
    }

    @Override
    public String test(boolean arg1, String arg2, char arg3, byte arg4, short arg5, int arg6, float arg7,
                       double arg8, long arg9) {
        return "boolean="+arg1+",String="+arg2+",char="+arg3+",byte="+arg4+",short="+arg5+",int="+arg6+",float="+arg7
                +",double="+arg8+",long="+arg9;
    }


}
