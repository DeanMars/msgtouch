package com.msgtouch.common.service;

import com.msgtouch.common.context.Constraint;
import com.msgtouch.network.annotation.MsgMethod;
import com.msgtouch.network.annotation.MsgService;
import com.msgtouch.network.annotation.Unauthorization;
import com.msgtouch.network.socket.packet.MsgPBPacket;
import com.msgtouch.network.socket.session.ISession;


/**
 * Created by Dean on 2016/10/12.
 */
@MsgService(Constraint.MSGTOUCH_TOUCHER)
public interface PBLoginService {


    @Unauthorization
    @MsgMethod(Constraint.TOUCHER_SERVICE_LOGIN)
    MsgPBPacket.Packet.Builder login(ISession session, MsgPBPacket.Packet.Builder packet);



}
