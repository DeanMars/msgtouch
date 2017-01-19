package com.msgtouch.network.socket.heartbeats;

import com.msgtouch.network.socket.session.ISession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Dean on 2017/1/16.
 */
public class MsgTouchClientHBHandler extends MsgTouchHBHandler {
    private static Logger logger= LoggerFactory.getLogger(MsgTouchClientHBHandler.class);


    void handlerReaderIdle(ISession session) {
        logger.info("MsgTouchClientHBHandler handlerReaderIdle channel={}",session.getChannel().toString());
    }

    void handlerWriterIdle(ISession session) {
        logger.info("MsgTouchClientHBHandler handlerWriterIdle channel={}",session.getChannel().toString());
        heartBeats(session);
    }

    void handlerAllIdle(ISession session) {
        logger.info("MsgTouchClientHBHandler handlerAllIdle channel={}",session.getChannel().toString());

    }

}
