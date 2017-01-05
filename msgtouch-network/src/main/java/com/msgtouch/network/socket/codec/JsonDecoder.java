package com.msgtouch.network.socket.codec;

import com.msgtouch.network.socket.packet.MsgType;
import com.msgtouch.network.socket.packet.MsgBytePacket;
import com.msgtouch.network.socket.packet.MsgPacket;
import com.msgtouch.network.utils.CodecUtils;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dean on 2016/9/8.
 */
public class JsonDecoder extends MsgDecoder<MsgBytePacket> {
    private static Logger logger= LoggerFactory.getLogger(JsonDecoder.class);

    protected void decode(ChannelHandlerContext channelHandlerContext, MsgBytePacket msgBytePacket, List list) throws Exception {

        boolean isCall=msgBytePacket.readBoolean();
        //uuid
        String uuid=msgBytePacket.readString();
        //命令字
        String cmd=msgBytePacket.readString();
        //消息类型
        MsgType msgType= MsgType.valueOf(msgBytePacket.readInt());
        //参数列表
        int paramsLength=msgBytePacket.readInt();
        List<Object> params=new ArrayList<Object>(paramsLength);
        for(int i=0;i<paramsLength;i++){
            boolean isNull=msgBytePacket.readBoolean();
            if(!isNull){
                String className=msgBytePacket.readString();
                Class clazz=Class.forName(className);

                Object param= CodecUtils.decode(msgType,clazz,msgBytePacket);
                params.add(param);
            }else{
                params.add(null);
            }
        }
        MsgPacket bilingPacket=new MsgPacket(cmd,params.toArray());
        bilingPacket.setCall(isCall);
        bilingPacket.setUuid(uuid);
        bilingPacket.setMsgType(msgType);


        logger.info("JsonDecoder.decode bilingPacket={}",bilingPacket.toString());

        list.add(bilingPacket);

    }

}
