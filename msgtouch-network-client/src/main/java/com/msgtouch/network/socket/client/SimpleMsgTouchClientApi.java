package com.msgtouch.network.socket.client;

import com.msgtouch.network.settings.SocketClientSetting;
import com.msgtouch.network.socket.NetClientEngine;
import com.msgtouch.network.socket.dispatcher.RpcCallBack;
import com.msgtouch.network.socket.listener.AbstractPBMsgPushedListener;
import com.msgtouch.network.socket.listener.MsgPushedListener;
import com.msgtouch.network.socket.packet.MsgPBPacket;
import com.msgtouch.network.socket.packet.MsgPacket;
import com.msgtouch.network.socket.session.ISession;
import com.msgtouch.network.socket.session.ISessionListenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by Dean on 2016/11/23.
 */
public class SimpleMsgTouchClientApi extends MsgTouchClientApi{
    private  static Logger logger= LoggerFactory.getLogger(SimpleMsgTouchClientApi.class);

    private static final SimpleMsgTouchClientApi simpleMsgTouchClientApi=new SimpleMsgTouchClientApi();

    private ITouchHostHelper iTouchHostHelper;



    private SimpleMsgTouchClientApi(){}

    public static SimpleMsgTouchClientApi getInstance(){
        return simpleMsgTouchClientApi;
    }


    public SimpleMsgTouchClientApi getSimpleApi(ITouchHostHelper iTouchHostHelper , SocketClientSetting socketClientSetting, AbstractPBMsgPushedListener ... pushedListeners){
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.iTouchHostHelper=iTouchHostHelper;
        if(iTouchHostHelper==null){
            iTouchHostHelper=new DefaultToucherHelper();
        }
        Toucher toucher=iTouchHostHelper.getToucher();
        socketClientSetting.host=toucher.getIpAddress();
        socketClientSetting.port=toucher.getPort();
        buildSimpleApi(socketClientSetting,pushedListeners);
        return simpleMsgTouchClientApi;
    }

    private void buildSimpleApi(final SocketClientSetting socketClientSetting, final AbstractPBMsgPushedListener ... pushedListeners){
        try {
            NetClientEngine.startSimplePBPacketClient(socketClientSetting);
            this.addPushedListener(pushedListeners);
            this.setSessionListener(new ISessionListenter() {
                public void sessionRegistered(ISession iSession) {
                    logger.info("ISessionListenter sessionRegistered: ");
                }

                public void sessionActive(ISession iSession) {
                    logger.info("ISessionListenter sessionActive: ");
                }

                public void sessionInActive(ISession iSession) {
                    logger.info("ISessionListenter sessionInActive: ");
                    shutdown(false);
                    buildSimpleApi(socketClientSetting,pushedListeners);
                }
            });
        } catch (Exception e) {
            logger.info("MsgTouchClientApi buildSimpleApi Exception={} ",e.getMessage());
            e.printStackTrace();
            getSimpleApi(iTouchHostHelper,socketClientSetting,pushedListeners);
        }
    }

}
