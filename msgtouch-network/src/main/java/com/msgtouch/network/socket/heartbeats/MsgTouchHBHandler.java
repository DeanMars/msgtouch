package com.msgtouch.network.socket.heartbeats;

import com.msgtouch.network.socket.packet.MsgPBPacket;
import com.msgtouch.network.socket.session.ISession;
import com.msgtouch.network.socket.session.Session;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Dean on 2017/1/16.
 */
public abstract class MsgTouchHBHandler extends SimpleChannelInboundHandler<MsgPBPacket.Packet.Builder> {
    private static Logger logger= LoggerFactory.getLogger(MsgTouchHBHandler.class);


    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MsgPBPacket.Packet.Builder builder) throws Exception {

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            ISession session=new Session(ctx.channel());
            if (event.state().equals(IdleState.READER_IDLE)) {
                handlerReaderIdle(session);
            } else if (event.state().equals(IdleState.WRITER_IDLE)) {
                handlerWriterIdle(session);
            } else if (event.state().equals(IdleState.ALL_IDLE)) {
                handlerAllIdle(session);
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    protected void heartBeats(ISession session){
        if(session.isActive()) {
            session.heartBeats(session.getHeartBeatTime(),session.getHeartBeatTimeUnit());
        }
    }

    abstract void handlerReaderIdle(ISession session);

    abstract void handlerWriterIdle(ISession session);

    abstract void handlerAllIdle(ISession session);
}
