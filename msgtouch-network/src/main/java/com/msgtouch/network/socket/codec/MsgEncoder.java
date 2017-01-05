package com.msgtouch.network.socket.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * Created by Dean on 2016/9/8.
 */
@ChannelHandler.Sharable
public class MsgEncoder<T> extends MessageToMessageEncoder<T> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, T t, List<Object> list) throws Exception {

    }

    public void writeString(String s,ByteBuf buf) throws Exception {
        byte[] arr=s.getBytes("UTF-8");
        int length=arr.length;
        buf.writeInt(length);
        buf.writeBytes(arr);
    }
}
