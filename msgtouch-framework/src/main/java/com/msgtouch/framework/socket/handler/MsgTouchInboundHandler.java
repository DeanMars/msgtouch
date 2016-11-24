package com.msgtouch.framework.socket.handler;

import com.msgtouch.framework.socket.dispatcher.MsgTouchMethodDispatcher;
import com.msgtouch.framework.socket.packet.MsgPacket;
import com.msgtouch.framework.socket.session.Session;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Dean on 2016/9/8.
 */

public class MsgTouchInboundHandler extends SimpleChannelInboundHandler<MsgPacket>{
    private static Logger logger= LoggerFactory.getLogger(MsgTouchInboundHandler.class);

    private MsgTouchMethodDispatcher msgTouchMethodDispatcher;

    public MsgTouchInboundHandler(){}

    public MsgTouchInboundHandler(MsgTouchMethodDispatcher msgTouchMethodDispatcher){
        this.msgTouchMethodDispatcher=msgTouchMethodDispatcher;
    }


    /**
     * 有客户端数据发送到服务端时会调用此方法，提交给自定义线程池处理业务逻辑。
     * @param  ctx
     * */
    protected void channelRead0(ChannelHandlerContext ctx, MsgPacket bilingPacket) throws Exception {
        logger.debug("MsgTouchInboundHandler channelRead0 bilingPacket={}",bilingPacket.toString());
        if(null!=msgTouchMethodDispatcher) {
            msgTouchMethodDispatcher.dispatcher(ctx, bilingPacket);
        }
    }


    /**
     * 连接断开是会调用此方法，方法会将Session相关信息移除，并且从Channel删除保存的Session对象
     * 并且会触发应用层扩展的离线时间
     * */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Session session=ctx.channel().attr(Session.SESSION_KEY).get();
        logger.debug("Channel inActive:{}",ctx.channel());

    }

    /**
     * 连接创建时会调用此方法，此时会负责创建ISession,并且为ISession分配一个Actor
     * */
    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        Session session=ctx.channel().attr(Session.SESSION_KEY).get();
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
        Session session=new Session(ctx);
        ctx.channel().attr(Session.SESSION_KEY).set(session);
    }

}
