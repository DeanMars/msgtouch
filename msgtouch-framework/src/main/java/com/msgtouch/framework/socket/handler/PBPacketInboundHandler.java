package com.msgtouch.framework.socket.handler;

import com.msgtouch.common.proto.MsgPBPacket;
import com.msgtouch.framework.socket.dispatcher.PBPacketMethodDispatcher;
import com.msgtouch.framework.socket.dispatcher.SyncRpcCallBack;
import com.msgtouch.framework.socket.session.ISession;
import com.msgtouch.framework.socket.session.Session;
import com.msgtouch.framework.socket.session.SessionManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * Created by Dean on 2016/9/8.
 */

public class PBPacketInboundHandler extends SimpleChannelInboundHandler<MsgPBPacket.Packet.Builder>{
    private static Logger logger= LoggerFactory.getLogger(PBPacketInboundHandler.class);

    private PBPacketMethodDispatcher msgTouchMethodDispatcher;

    public PBPacketInboundHandler(PBPacketMethodDispatcher msgTouchMethodDispatcher){
        this.msgTouchMethodDispatcher=msgTouchMethodDispatcher;
    }


    /**
     * 有客户端数据发送到服务端时会调用此方法，提交给自定义线程池处理业务逻辑。
     * @param  ctx
     * */
    protected void channelRead0(ChannelHandlerContext ctx, MsgPBPacket.Packet.Builder packet) throws Exception {
        logger.debug("JsonPacketInboundHandler channelRead0 bilingPacket={}",packet.toString());
        ISession session=ctx.channel().attr(Session.SESSION_KEY).get();
        if(null!=msgTouchMethodDispatcher) {
            msgTouchMethodDispatcher.dispatcher(session, packet);
        }
    }


    /**
     * 连接断开是会调用此方法，方法会将Session相关信息移除，并且从Channel删除保存的Session对象
     * 并且会触发应用层扩展的离线时间
     * */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ISession session=ctx.channel().attr(Session.SESSION_KEY).get();
        SessionManager.getInstance().removeSession(session);
        logger.debug("Channel inActive:{}",ctx.channel());

    }

    /**
     * 连接创建时会调用此方法，此时会负责创建ISession,并且为ISession分配一个Actor
     * */
    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        ISession session=ctx.channel().attr(Session.SESSION_KEY).get();
        SessionManager.getInstance().addAnonymousSession(session);
        logger.debug("ChannelActive:{}",ctx.channel().toString());
    }
    /**
     * 出现异常时
     * */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        final ISession session=new Session(ctx.channel());
        ctx.channel().attr(Session.SESSION_KEY).set(session);
        session.setAttribute(Session.SYNC_CALLBACK_MAP, new HashMap<String, SyncRpcCallBack<?>>());
    }

}
