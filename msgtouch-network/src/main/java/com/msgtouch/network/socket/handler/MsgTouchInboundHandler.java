package com.msgtouch.network.socket.handler;

import com.msgtouch.network.socket.dispatcher.MethodDispatcher;
import com.msgtouch.network.socket.dispatcher.RpcCallBack;
import com.msgtouch.network.socket.dispatcher.SyncRpcCallBack;
import com.msgtouch.network.socket.packet.MsgPBPacket;
import com.msgtouch.network.socket.session.ISession;
import com.msgtouch.network.socket.session.ISessionListenter;
import com.msgtouch.network.socket.session.Session;
import com.msgtouch.network.socket.session.SessionManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by Dean on 2016/9/8.
 */

public class MsgTouchInboundHandler extends SimpleChannelInboundHandler<MsgPBPacket.Packet.Builder>{
    private static Logger logger= LoggerFactory.getLogger(MsgTouchInboundHandler.class);

    private MethodDispatcher msgTouchMethodDispatcher;

    public MsgTouchInboundHandler(MethodDispatcher msgTouchMethodDispatcher){
        this.msgTouchMethodDispatcher=msgTouchMethodDispatcher;
    }


    /**
     * 有客户端数据发送到服务端时会调用此方法，提交给自定义线程池处理业务逻辑。
     * @param  ctx
     * */
    protected void channelRead0(ChannelHandlerContext ctx, MsgPBPacket.Packet.Builder packet) throws Exception {
        logger.debug("MsgTouchInboundHandler channelRead0 MsgPBPacket={}",packet.toString());
        ISession session=ctx.channel().attr(Session.SESSION_KEY).get();
        if(null!=msgTouchMethodDispatcher) {
            msgTouchMethodDispatcher.dispatcher(session, packet);
        }
    }


    /**
     * 连接断开是会调用此方法，方法会将Session相关信息移除，并且从Channel删除保存的Session对象
     * */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ISession session=ctx.channel().attr(Session.SESSION_KEY).get();
        //session.cancelHeartBeats();
        SessionManager.getInstance().removeSession(session);
        ISessionListenter sessionListenter=session.getSessionLisenter();
        if(null!=sessionListenter){
            sessionListenter.sessionInActive(session);
        }
        logger.info("Channel inActive:{}",ctx.channel());
    }

    /**
     * 连接创建时会调用此方法，此时会负责创建ISession
     * */
    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        ISession session=ctx.channel().attr(Session.SESSION_KEY).get();
        SessionManager.getInstance().addAnonymousSession(session);
        ISessionListenter sessionListenter=session.getSessionLisenter();
        if(null!=sessionListenter){
            sessionListenter.sessionActive(session);
        }
        logger.info("Channel Active:{}",ctx.channel().toString());
    }
    /**
     * 出现异常时
     * */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        logger.error("MsgTouchInboundHandler exceptionCaught channel:{},cause={}",ctx.channel().toString(),cause.getMessage());
        cause.printStackTrace();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        ISession session=new Session(ctx.channel());
        ctx.channel().attr(Session.SESSION_KEY).set(session);
        session.setAttribute(Session.SYNC_CALLBACK_MAP, new HashMap<String, SyncRpcCallBack<?>>());
        session.setAttribute(Session.ASYNC_CALLBACK_MAP, new HashMap<String,RpcCallBack>());
        ISessionListenter sessionListenter=session.getSessionLisenter();
        if(null!=sessionListenter){
            sessionListenter.sessionRegistered(session);
        }
    }

}
