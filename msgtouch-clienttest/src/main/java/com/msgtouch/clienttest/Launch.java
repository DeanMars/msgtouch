package com.msgtouch.clienttest;

import com.msgtouch.common.context.Constraint;
import com.msgtouch.framework.settings.SocketClientSetting;
import com.msgtouch.framework.socket.SocketEngine;
import com.msgtouch.framework.socket.client.AbstractPBMsgPushedListener;
import com.msgtouch.framework.socket.client.MsgTouchClientApi;
import com.msgtouch.framework.socket.packet.MsgPBPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Dean on 2016/10/9.
 */

public class Launch {

    private  static Logger logger= LoggerFactory.getLogger(Launch.class);
    public static void main(String []args){
        try {

            startPBClient();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private static SocketClientSetting getSocketClientSetting(){
        SocketClientSetting socketClientSetting=new SocketClientSetting();
        socketClientSetting.host="127.0.0.1";
        socketClientSetting.port=8001;
        socketClientSetting.timeOutSecond=20;
        return socketClientSetting;
    }

    private static void startJsonClient() throws Exception{
        SocketClientSetting socketClientSetting=getSocketClientSetting();
        MsgTouchClientApi msgTouchClientApi=SocketEngine.startJsonPacketClient(socketClientSetting);
        //LoginService loginService=msgTouchClientApi.getRpcCallProxy(true, LoginService.class);
        // logger.info(loginService.login("32432423423"));
        // logger.info(loginService.test(true,"1",'2',(byte)1,(short)2,3,4.0f,5.0,6L));
        //msgTouchClientApi.addPushedListener(new PushListener());

    }


    private static void startPBClient() throws Exception{
        SocketClientSetting socketClientSetting=getSocketClientSetting();
        MsgTouchClientApi msgTouchClientApi=SocketEngine.startPBPacketClient(socketClientSetting);
        msgTouchClientApi.addPushedListener(new AbstractPBMsgPushedListener() {
            @Override
            public void msgReceived0(MsgPBPacket.Packet.Builder packet) {
                logger.info("AbstractPBMsgPushedListener msgReceived0 packet.cmd={}",packet.getCmd());
            }
        });



        MsgPBPacket.Packet.Builder builder= MsgPBPacket.Packet.newBuilder();

        builder.setUid(12423423L);
        builder.setCustomerId("fdgfdgfdgfd");

        MsgPBPacket.Packet.Builder ret=msgTouchClientApi.syncRpcCall(Constraint.MSGTOUCH_TOUCHER,"pbTest",builder);


        return;
    }


}
