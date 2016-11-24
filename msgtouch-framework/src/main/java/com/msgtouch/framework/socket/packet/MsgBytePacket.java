package com.msgtouch.framework.socket.packet;

import io.netty.buffer.ByteBuf;

public class MsgBytePacket {
    private ByteBuf content;
    public MsgBytePacket(ByteBuf content) {
        this.content=content;
    }
    public ByteBuf getContent() {
        return content;
    }
}
