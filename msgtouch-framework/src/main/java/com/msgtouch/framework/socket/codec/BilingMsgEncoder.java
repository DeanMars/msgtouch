package com.msgtouch.framework.socket.codec;

import com.msgtouch.framework.socket.packet.MsgBytePacket;
import com.msgtouch.framework.socket.packet.MsgPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Dean on 2016/9/8.
 */
public class BilingMsgEncoder extends MessageToMessageEncoder<MsgPacket> {
    private static Logger logger= LoggerFactory.getLogger(BilingMsgDecoder.class);

    protected void encode(ChannelHandlerContext ctx, MsgPacket bilingPacket, List list) throws Exception {
        try{

            ByteBuf buf= Unpooled.buffer();
            buf.writeBoolean(bilingPacket.isCall());
            writeString(bilingPacket.getPackageId(),buf);
            writeString(bilingPacket.getGameId(),buf);
            writeString(bilingPacket.getAvdNo(),buf);
            buf.writeInt(bilingPacket.getUid());
            writeString(bilingPacket.getSdkVersion(),buf);
            writeString(bilingPacket.getCmd(),buf);
            buf.writeInt(bilingPacket.getMsgType().value);
            buf.writeBoolean(bilingPacket.getFromCilent());
            writeString(bilingPacket.getParams(),buf);

            MsgBytePacket bilingBytePacket=new MsgBytePacket(buf);
            list.add(bilingBytePacket);
            logger.debug("RpcService encode {}:{}",ctx.channel(),bilingPacket.toString());
        }catch (Error e){
            e.printStackTrace();
            logger.error("RpcService encode error {}",ctx.channel(),e);
        }
    }

    public void writeString(String s,ByteBuf buf) throws Exception {
        byte[] arr=s.getBytes("UTF-8");
        int length=arr.length;
        buf.writeInt(length);
        buf.writeBytes(arr);
    }
}
