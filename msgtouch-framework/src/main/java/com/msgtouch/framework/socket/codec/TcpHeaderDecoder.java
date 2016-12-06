package com.msgtouch.framework.socket.codec;

import com.msgtouch.framework.socket.packet.MsgBytePacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * 将ByteBuf封装为BinaryPacket
 */
public class TcpHeaderDecoder extends ReplayingDecoder<Void> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int length=in.readInt();
        MsgBytePacket packet=new MsgBytePacket(in.readBytes(length));
        out.add(packet);
    }
}
