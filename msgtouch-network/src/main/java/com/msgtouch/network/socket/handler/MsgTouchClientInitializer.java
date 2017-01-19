package com.msgtouch.network.socket.handler;

import com.msgtouch.network.settings.SocketClientSetting;
import com.msgtouch.network.socket.codec.TcpHeaderDecoder;
import com.msgtouch.network.socket.dispatcher.MethodDispatcher;
import com.msgtouch.network.socket.heartbeats.MsgTouchClientHBHandler;
import com.msgtouch.network.socket.session.ISessionListenter;
import com.msgtouch.network.socket.session.Session;
import com.msgtouch.network.socket.codec.MsgDecoder;
import com.msgtouch.network.socket.codec.MsgEncoder;
import com.msgtouch.network.socket.codec.TcpHeaderEncoder;
import com.msgtouch.network.utils.EngineParams;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * Created by Dean on 2016/9/22.
 */
public class MsgTouchClientInitializer extends ChannelInitializer<SocketChannel> {
    private SocketClientSetting settings;
    private MsgDecoder msgTouchDecoder;
    private MsgEncoder msgTouchEncoder;
    private ISessionListenter sessionLisenter;
    private MethodDispatcher msgTouchMethodDispatcher;

    public MsgTouchClientInitializer(SocketClientSetting settings, MethodDispatcher msgTouchMethodDispatcher,
                                     MsgDecoder msgTouchDecoder, MsgEncoder msgTouchEncoder){
        this.settings=settings;
        this.msgTouchMethodDispatcher=msgTouchMethodDispatcher;
        this.msgTouchDecoder=msgTouchDecoder;
        this.msgTouchEncoder=msgTouchEncoder;
    }


    public MsgTouchClientInitializer(SocketClientSetting settings, MethodDispatcher msgTouchMethodDispatcher,
                                     MsgDecoder msgTouchDecoder, MsgEncoder msgTouchEncoder,ISessionListenter sessionLisenter){
        this.settings=settings;
        this.msgTouchMethodDispatcher=msgTouchMethodDispatcher;
        this.msgTouchDecoder=msgTouchDecoder;
        this.msgTouchEncoder=msgTouchEncoder;
        this.sessionLisenter=sessionLisenter;
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

        ch.pipeline().addLast(new IdleStateHandler(10, 10, 15, TimeUnit.SECONDS));
        ch.pipeline().addLast(new MsgTouchClientHBHandler());

        ch.attr(Session.SECRRET_KEY).set(settings.secretKey);


    }
}
