package com.msgtouch.framework.socket.handler;

import com.msgtouch.framework.settings.SocketClientSetting;
import com.msgtouch.framework.socket.codec.*;
import com.msgtouch.framework.socket.dispatcher.MethodDispatcher;
import com.msgtouch.framework.socket.session.Session;
import com.msgtouch.framework.utils.EngineParams;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * Created by Dean on 2016/9/22.
 */
public class MsgTouchClientInitializer extends ChannelInitializer<SocketChannel> {
    private SocketClientSetting settings;
    private MsgDecoder msgTouchDecoder;
    private MsgEncoder msgTouchEncoder;

    private MethodDispatcher msgTouchMethodDispatcher;

    public MsgTouchClientInitializer(SocketClientSetting settings, MethodDispatcher msgTouchMethodDispatcher,
                                     MsgDecoder msgTouchDecoder, MsgEncoder msgTouchEncoder){
        this.settings=settings;
        this.msgTouchMethodDispatcher=msgTouchMethodDispatcher;
        this.msgTouchDecoder=msgTouchDecoder;
        this.msgTouchEncoder=msgTouchEncoder;
    }

    protected void initChannel(SocketChannel ch) throws Exception {
        if(EngineParams.isNettyLogging()){
            ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
        }
        ch.pipeline().addLast(new TcpHeaderDecoder());
        ch.pipeline().addLast(msgTouchDecoder);
        ch.pipeline().addLast(new TcpHeaderEncoder());
        ch.pipeline().addLast(msgTouchEncoder);

        ch.pipeline().addLast(new MsgTouchInboundHandler(msgTouchMethodDispatcher));
        ch.attr(Session.SECRRET_KEY).set(settings.secretKey);


    }
}
