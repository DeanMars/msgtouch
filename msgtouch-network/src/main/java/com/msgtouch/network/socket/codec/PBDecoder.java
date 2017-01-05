package com.msgtouch.network.socket.codec;

import com.msgtouch.network.socket.packet.MsgBytePacket;
import com.msgtouch.network.socket.packet.MsgPBPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Dean on 2016/9/8.
 */
public class PBDecoder extends MsgDecoder<MsgBytePacket> {
    private static Logger logger= LoggerFactory.getLogger(PBDecoder.class);

    protected void decode(ChannelHandlerContext channelHandlerContext, MsgBytePacket msgBytePacket, List list) throws Exception {
        ByteBuf byteBuf= msgBytePacket.getContent();
        int length=byteBuf.readableBytes();
        byte[] dst=new byte[length];
        byteBuf.readBytes(dst);
        MsgPBPacket.Packet.Builder builder= MsgPBPacket.Packet.newBuilder();
        builder.mergeFrom(dst);
        list.add(builder);
    }


}
