package com.msgtouch.common.service;

import com.msgtouch.common.context.Constraint;
import com.msgtouch.network.annotation.MsgMethod;
import com.msgtouch.network.annotation.MsgService;
import com.msgtouch.network.socket.packet.MsgPBPacket;


/**
 * Created by Dean on 2016/10/12.
 */
@MsgService(Constraint.MSGTOUCH_TOUCHER)
public interface PBTestService {
    @MsgMethod("pbTest")
    MsgPBPacket.Packet.Builder pbTest(MsgPBPacket.Packet.Builder  packet);



}
