package com.msgtouch.network.socket.packet;

import com.msgtouch.network.context.Constraint;

/**
 * Created by Dean on 2017/1/13.
 */
public class MsgHB {
    private static MsgPBPacket.Packet.Builder builder;
    public static MsgPBPacket.Packet.Builder getHBPacket(){
        if(null==builder) {
            synchronized (MsgHB.class){
                if(null==builder){
                    builder = MsgPBPacket.Packet.newBuilder();
                    builder.setMsgType(MsgPBPacket.MsgType.Request);
                    builder.setCmd(Constraint.MsgTouchHeartBeats);
                }
            }
        }
        return builder;
    }
}
