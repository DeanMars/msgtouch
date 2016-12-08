package com.msgtouch.framework.socket.codec;

import com.msgtouch.framework.socket.packet.MsgBytePacket;
import com.msgtouch.framework.socket.packet.MsgPBPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Dean on 2016/9/8.
 */
public class PBEncoder extends MsgEncoder<MsgPBPacket.Packet.Builder> {
    private static Logger logger= LoggerFactory.getLogger(JsonDecoder.class);
    protected void encode(ChannelHandlerContext ctx, MsgPBPacket.Packet.Builder builder, List list) throws Exception {
        try{
            ByteBuf buf= Unpooled.buffer();
            MsgPBPacket.Packet packet=builder.build();
            byte[] datas=packet.toByteArray();
            buf.writeBytes(datas);
            MsgBytePacket msgBytePacket=new MsgBytePacket(buf);
            list.add(msgBytePacket);
            logger.debug("RpcService encode {}:{}",ctx.channel(),packet.toString());
        }catch (Error e){
            e.printStackTrace();
            logger.error("RpcService encode error {}",ctx.channel(),e);
        }
    }

}
