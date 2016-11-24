package com.msgtouch.framework.socket.codec;

import com.msgtouch.framework.socket.packet.MsgType;
import com.msgtouch.framework.socket.packet.MsgBytePacket;
import com.msgtouch.framework.socket.packet.MsgPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by Dean on 2016/9/8.
 */
public class BilingMsgDecoder extends MessageToMessageDecoder<MsgBytePacket>{
    private static Logger logger= LoggerFactory.getLogger(BilingMsgDecoder.class);

    protected void decode(ChannelHandlerContext channelHandlerContext, MsgBytePacket bilingBytePacket, List list) throws Exception {
        ByteBuf byteBuf=bilingBytePacket.getContent();
        boolean isCall=byteBuf.readBoolean();
        //uuid
        String uuid=readString(byteBuf);
        //游戏编号
        String gameId=readString(byteBuf);
        //渠道号
        String avdNo=readString(byteBuf);
        //用户id
        int uid=byteBuf.readInt();
        //sdk版本号
        String sdkVersion=readString(byteBuf);
        //命令字
        String cmd=readString(byteBuf);
        //消息类型
        MsgType msgType= MsgType.valueOf(byteBuf.readInt());
        //消息来源
        boolean fromClient=byteBuf.readBoolean();
        //参数列表
        String params=readString(byteBuf);

        MsgPacket bilingPacket=new MsgPacket(cmd,params);
        bilingPacket.setCall(isCall);
        bilingPacket.setPackageId(uuid);
        bilingPacket.setGameId(gameId);
        bilingPacket.setAvdNo(avdNo);
        bilingPacket.setUid(uid);
        bilingPacket.setSdkVersion(sdkVersion);
        bilingPacket.setMsgType(msgType);
        bilingPacket.setFromCilent(fromClient);

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
