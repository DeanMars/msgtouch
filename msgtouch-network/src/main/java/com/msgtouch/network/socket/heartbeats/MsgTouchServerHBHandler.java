package com.msgtouch.network.socket.heartbeats;

import com.msgtouch.network.socket.session.ISession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Dean on 2017/1/16.
 */
public class MsgTouchServerHBHandler extends MsgTouchHBHandler {
    private static Logger logger= LoggerFactory.getLogger(MsgTouchServerHBHandler.class);


    void handlerReaderIdle(ISession session) {
        logger.info("MsgTouchServerHBHandler handlerReaderIdle channel={}",session.getChannel().toString());
        if(session.isActive()){
            session.getChannel().close();
        }
    }

    void handlerWriterIdle(ISession session) {
        logger.info("MsgTouchServerHBHandler handlerWriterIdle channel={}",session.getChannel().toString());

    }

    void handlerAllIdle(ISession session) {
        logger.info("MsgTouchServerHBHandler handlerAllIdle channel={}", session.getChannel().toString());
        if(session.isActive()){
            session.getChannel().close();
        }
    }
}
