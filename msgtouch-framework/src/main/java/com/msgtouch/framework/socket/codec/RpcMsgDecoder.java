package com.msgtouch.framework.socket.codec;

import com.alibaba.fastjson.JSON;
import com.msgtouch.framework.socket.packet.MsgType;
import com.msgtouch.framework.socket.packet.MsgBytePacket;
import com.msgtouch.framework.socket.packet.MsgPacket;
import com.msgtouch.framework.utils.CodecUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dean on 2016/9/8.
 */
public class BilingMsgDecoder extends MessageToMessageDecoder<MsgBytePacket>{
    private static Logger logger= LoggerFactory.getLogger(BilingMsgDecoder.class);

    protected void decode(ChannelHandlerContext channelHandlerContext, MsgBytePacket msgBytePacket, List list) throws Exception {
        ByteBuf byteBuf=msgBytePacket.getContent();
        boolean isCall=byteBuf.readBoolean();
        //uuid
        String uuid=readString(byteBuf);
        //命令字
        String cmd=readString(byteBuf);
        //消息类型
        MsgType msgType= MsgType.valueOf(byteBuf.readInt());
        //参数列表
        int paramsLength=byteBuf.readInt();
        List<Object> params=new ArrayList<Object>(paramsLength);
        for(int i=0;i<paramsLength;i++){
            boolean isNull=byteBuf.readBoolean();
            if(!isNull){
                String className=readString(byteBuf);
                Class clazz=Class.forName(className);
                String value=readString(byteBuf);
                Object param= CodecUtils.decode(msgType,clazz,value);
                params.add(param);
            }else{
                params.add(null);
            }
        }
        MsgPacket bilingPacket=new MsgPacket(cmd,params.toArray());
        bilingPacket.setCall(isCall);
        bilingPacket.setUuid(uuid);
        bilingPacket.setMsgType(msgType);


        logger.info("BilingMsgDecoder.decode bilingPacket={}",bilingPacket.toString());

        list.add(bilingPacket);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        logger.error("BilingMsgDecoder Exception Caught {}",ctx.channel(),cause);
    }

    private String readString(ByteBuf byteBuf) throws UnsupportedEncodingException{
        int length=byteBuf.readInt();
        byte[] content=byteBuf.readBytes(length).array();
        return new String(content,"UTF-8");
    }
}
