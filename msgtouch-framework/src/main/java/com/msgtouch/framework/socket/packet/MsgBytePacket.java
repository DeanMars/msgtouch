package com.msgtouch.framework.socket.packet;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;

import java.io.UnsupportedEncodingException;

public class MsgBytePacket {
    private ByteBuf content;
    public MsgBytePacket(ByteBuf content) {
        this.content=content;
    }
    public ByteBuf getContent() {
        return content;
    }

    public boolean readBoolean(){
        return content.readBoolean();
    }

    public int readInt(){
        return content.readInt();
    }

    public String readString() throws UnsupportedEncodingException {
        int length=content.readInt();
        byte[] contents=content.readBytes(length).array();
        return new String(contents,"UTF-8");
    }

    public Message.Builder readProtoBuf(Message.Builder builder) {
        int length=content.readInt();
        byte[] dst=new byte[length];
        content.readBytes(dst);
        try {
            builder.mergeFrom(dst);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        builder.build();
        return builder;
    }
}
