package com.msgtouch.toucher.service;

import com.msgtouch.common.service.PBLoginService;
import com.msgtouch.framework.registry.ConsulEngine;
import com.msgtouch.network.socket.packet.MsgPBPacket;
import com.msgtouch.network.socket.session.ISession;
import com.msgtouch.network.socket.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by Dean on 2016/10/9.
 */
@Service
public class PBLoginServiceImpl implements PBLoginService {

    private  static Logger logger= LoggerFactory.getLogger(PBLoginServiceImpl.class);

    @Override
    public MsgPBPacket.Packet.Builder login(ISession session, MsgPBPacket.Packet.Builder packet) {
        long uid=packet.getUid();
        String gameId=packet.getGameId();
        String customerId=packet.getCustomerId();
        if(session.isActive()){
            //验证登陆逻辑


            //绑定用户session
            String userKey= SessionManager.getInstance().getUserKey(uid,gameId);
            SessionManager.getInstance().regesterSession(userKey,session,true);
            ConsulEngine.getInstance().loginApp(uid,gameId);
        }
        packet.setRetCode(MsgPBPacket.RetCode.OK);
        return packet;
    }
}
