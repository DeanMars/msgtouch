package com.msgtouch.network.socket.codec;

import com.msgtouch.network.socket.packet.MsgBytePacket;
import com.msgtouch.network.socket.packet.MsgPacket;
import com.msgtouch.network.utils.CodecUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Dean on 2016/9/8.
 */
public class JsonEncoder extends MsgEncoder<MsgPacket> {
    private static Logger logger= LoggerFactory.getLogger(JsonEncoder.class);
    protected void encode(ChannelHandlerContext ctx, MsgPacket msgPacket, List list) throws Exception {
        try{
            ByteBuf buf= Unpooled.buffer();
            buf.writeBoolean(msgPacket.isCall());
            writeString(msgPacket.getUuid(),buf);
            writeString(msgPacket.getCmd(),buf);
            buf.writeInt(msgPacket.getMsgType().value);
            Object[] params=msgPacket.getParams();
            buf.writeInt(params.length);
            for(int i=0;i<params.length;i++){
                Object e=params[i];
                boolean isNull=e==null;
                buf.writeBoolean(isNull);
                if(!isNull){
                    Class clazz=e.getClass();
                    writeString(clazz.getName(),buf);
                    String value= CodecUtils.encode(msgPacket.getMsgType(),clazz,e);
                    writeString(value,buf);
                }
            }
            MsgBytePacket msgBytePacket=new MsgBytePacket(buf);
            list.add(msgBytePacket);
            logger.debug("JsonEncoder encode {}:{}",ctx.channel(),msgPacket.toString());
        }catch (Error e){
            e.printStackTrace();
            logger.error("JsonEncoder encode error {}",ctx.channel(),e);
        }
    }

}
