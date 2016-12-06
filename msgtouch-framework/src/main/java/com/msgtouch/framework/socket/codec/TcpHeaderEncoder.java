package com.msgtouch.framework.socket.codec;

import com.msgtouch.framework.socket.packet.MsgBytePacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by Dean on 2016/9/9.
 */
public class TcpHeaderEncoder extends MessageToByteEncoder<MsgBytePacket> {
    protected void encode(ChannelHandlerContext channelHandlerContext, MsgBytePacket packet, ByteBuf out) throws Exception {
        ByteBuf buf= Unpooled.buffer();
        buf.writeInt(packet.getContent().readableBytes());
        buf.writeBytes(packet.getContent());
        out.writeBytes(buf);
        packet.getContent().release();
    }
}
