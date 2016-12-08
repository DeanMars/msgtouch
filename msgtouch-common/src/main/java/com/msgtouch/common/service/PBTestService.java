package com.msgtouch.common.service;

import com.msgtouch.common.context.Constraint;
import com.msgtouch.framework.annotation.MsgMethod;
import com.msgtouch.framework.annotation.MsgService;
import com.msgtouch.framework.socket.packet.MsgPBPacket;

/**
 * Created by Dean on 2016/10/12.
 */
@MsgService(Constraint.MSGTOUCH_TOUCHER)
public interface PBTestService {
    @MsgMethod("pbTest")
    MsgPBPacket.Packet.Builder pbTest(MsgPBPacket.Packet.Builder  packet);



}
