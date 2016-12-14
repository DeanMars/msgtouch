package com.msgtouch.common.service;

import com.msgtouch.common.context.Constraint;
import com.msgtouch.framework.annotation.MsgMethod;
import com.msgtouch.framework.annotation.MsgService;
import com.msgtouch.framework.socket.packet.MsgPBPacket;
import com.msgtouch.framework.socket.session.ISession;

/**
 * Created by Dean on 2016/10/12.
 */
@MsgService(Constraint.MSGTOUCH_TOUCHER)
public interface PBLoginService {
    @MsgMethod("pbTest")
    MsgPBPacket.Packet.Builder login(ISession session,MsgPBPacket.Packet.Builder packet);



}
