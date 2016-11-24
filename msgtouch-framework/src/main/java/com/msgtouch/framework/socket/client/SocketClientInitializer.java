package com.msgtouch.framework.socket.client;

import com.msgtouch.framework.socket.codec.BilingHeaderDecoder;
import com.msgtouch.framework.socket.codec.BilingHeaderEncoder;
import com.msgtouch.framework.socket.codec.BilingMsgDecoder;
import com.msgtouch.framework.socket.codec.BilingMsgEncoder;
import com.msgtouch.framework.socket.dispatcher.MsgTouchMethodDispatcher;
import com.msgtouch.framework.socket.handler.MsgTouchInboundHandler;
import com.msgtouch.framework.socket.session.Session;
import com.msgtouch.framework.utils.EngineParams;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * Created by Dean on 2016/9/22.
 */
public class SocketClientInitializer extends ChannelInitializer<SocketChannel> {
    private SocketClientSetting settings;

    private MsgTouchMethodDispatcher msgTouchMethodDispatcher;

    public SocketClientInitializer(SocketClientSetting settings){
        this.settings=settings;
    }

    protected void initChannel(SocketChannel ch) throws Exception {
        if(EngineParams.isNettyLogging()){
            ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
        }
        ch.pipeline().addLast(new BilingHeaderDecoder());
        ch.pipeline().addLast(new BilingMsgDecoder());
        ch.pipeline().addLast(new BilingHeaderEncoder());
        ch.pipeline().addLast(new BilingMsgEncoder());

        ch.pipeline().addLast(new MsgTouchInboundHandler());
        ch.attr(Session.SECRRET_KEY).set(settings.secretKey);


    }
}
