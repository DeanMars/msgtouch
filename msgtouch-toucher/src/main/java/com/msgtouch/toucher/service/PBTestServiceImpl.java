package com.msgtouch.toucher.service;

import com.google.protobuf.InvalidProtocolBufferException;
import com.msgtouch.common.proto.MsgTest;
import com.msgtouch.common.service.PBTestService;
import com.msgtouch.framework.socket.packet.MsgPBPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by Dean on 2016/10/9.
 */
@Service
public class PBTestServiceImpl implements PBTestService {

    private  static Logger logger= LoggerFactory.getLogger(PBTestServiceImpl.class);
    @Override
    public MsgPBPacket.Packet.Builder pbTest(MsgPBPacket.Packet.Builder packet) {
        try {
            MsgTest.MsgTestRequest msgTestRequest = MsgTest.MsgTestRequest.parseFrom(packet.getEBody());
            String msg=msgTestRequest.getMsg();
            logger.info("MsgPBPacket.Packet.Builder msg={},packet={}",msg,packet.toString());


            MsgTest.MsgTestResponse.Builder response= MsgTest.MsgTestResponse.newBuilder();
            response.setMsg("server received:"+msgTestRequest.getMsg());
            packet.setEBody(response.build().toByteString());
            packet.setRetCode(MsgPBPacket.RetCode.OK);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return packet;
    }
}
