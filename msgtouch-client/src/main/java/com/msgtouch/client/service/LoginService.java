package com.msgtouch.client.service;

import com.msgtouch.framework.annotation.MsgMethod;
import com.msgtouch.framework.annotation.MsgService;
import com.msgtouch.framework.socket.packet.MsgPacket;

/**
 * Created by Dean on 2016/10/20.
 */
@MsgService("loginService")
public interface LoginService {

    @MsgMethod("login")
    MsgPacket login(MsgPacket msgPacket);

}
