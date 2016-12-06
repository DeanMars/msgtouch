package com.msgtouch.framework.socket.codec;

import com.msgtouch.common.proto.MsgPBPacket;
import com.msgtouch.framework.socket.packet.MsgBytePacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Dean on 2016/9/8.
 */
public class PBDecoder extends MessageToMessageDecoder<MsgBytePacket>{
    private static Logger logger= LoggerFactory.getLogger(PBDecoder.class);

    protected void decode(ChannelHandlerContext channelHandlerContext, MsgBytePacket msgBytePacket, List list) throws Exception {
        ByteBuf byteBuf= msgBytePacket.getContent();
        int length=byteBuf.readableBytes();
        byte[] dst=new byte[length];
        byteBuf.readBytes(dst);
        MsgPBPacket.Packet.Builder builder=MsgPBPacket.Packet.newBuilder();
        builder.mergeFrom(dst);
        list.add(builder);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        logger.error("RpcMsgDecoder Exception Caught {}",ctx.channel(),cause);
    }

}
