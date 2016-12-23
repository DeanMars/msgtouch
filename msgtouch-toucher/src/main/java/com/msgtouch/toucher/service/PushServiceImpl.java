package com.msgtouch.toucher.service;

import com.msgtouch.common.proto.MsgTest;
import com.msgtouch.common.vo.TestVo;
import com.msgtouch.framework.socket.packet.MsgPBPacket;
import com.msgtouch.framework.socket.session.ISession;
import com.msgtouch.framework.socket.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Dean on 2016/11/29.
 */
@Service
public class PushServiceImpl {
    private  static Logger logger= LoggerFactory.getLogger(PushServiceImpl.class);

    public void pushAll(String msg){
        Collection<ISession> list=SessionManager.getInstance().getAllSession();

        TestVo testVo=new TestVo();
        testVo.setRequest(msg);

        for(ISession session:list){
            try {
                TestVo ret= session.pushJsonMsg( testVo, 10);
                logger.info("pushAll ret testVo Request={},Response={}",ret.getRequest(),ret.getResponse());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }

    }

    public void pushPBAll(String msg){
        Collection<ISession> list=SessionManager.getInstance().getAllSession();
        MsgPBPacket.Packet.Builder packet=buildTestPacket(msg);
        for(ISession session:list){
            pushMsg(session,packet);
        }

    }


    public void pushMsg(String msg,long uid,String gameId){
        String userKey=SessionManager.getInstance().getUserKey(uid,gameId);
        ISession session=SessionManager.getInstance().getSession(userKey);
        if(null!=session){
            MsgPBPacket.Packet.Builder packet=buildTestPacket(msg);
            pushMsg(session,packet);
        }
    }


    private void pushMsg(ISession session,MsgPBPacket.Packet.Builder packet){
        try {
            MsgPBPacket.Packet.Builder ret= session.pushPBMsg(packet, 10);
            logger.info("pushAll ret testVo Request={},Response={}",ret.getUid(),ret.getCustomerId());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    private MsgPBPacket.Packet.Builder buildTestPacket(String msg){
        MsgPBPacket.Packet.Builder packet=MsgPBPacket.Packet.newBuilder();
        MsgTest.MsgTestRequest.Builder req=MsgTest.MsgTestRequest.newBuilder();
        req.setMsg(msg);
        packet.setEBody(req.build().toByteString());
        //packet.setCmd("#msgPushed");
        return packet;
    }
}
