package com.msgtouch.framework.socket.client;

import com.msgtouch.framework.socket.packet.MsgPBPacket;

/**
 * Created by Dean on 2016/12/7.
 */
public abstract class AbstractPBMsgPushedListener implements MsgPushedListener<MsgPBPacket.Packet.Builder> {
    @Override
    public MsgPBPacket.Packet.Builder msgReceived(MsgPBPacket.Packet.Builder builder) {
        msgReceived0(builder);
        builder.setRetCode(MsgPBPacket.RetCode.OK);
        return builder;
    }


    public abstract void msgReceived0(MsgPBPacket.Packet.Builder builder);
}
