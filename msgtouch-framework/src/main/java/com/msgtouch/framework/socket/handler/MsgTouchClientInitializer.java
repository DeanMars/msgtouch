package com.msgtouch.framework.socket.handler;

import com.msgtouch.framework.settings.SocketClientSetting;
import com.msgtouch.framework.socket.codec.TcpHeaderDecoder;
import com.msgtouch.framework.socket.codec.TcpHeaderEncoder;
import com.msgtouch.framework.socket.codec.RpcMsgDecoder;
import com.msgtouch.framework.socket.codec.RpcMsgEncoder;
import com.msgtouch.framework.socket.dispatcher.JsonPacketMethodDispatcher;
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

    private JsonPacketMethodDispatcher msgTouchMethodDispatcher;

    public MsgTouchClientInitializer(SocketClientSetting settings, JsonPacketMethodDispatcher msgTouchMethodDispatcher){
        this.settings=settings;
        this.msgTouchMethodDispatcher=msgTouchMethodDispatcher;
    }

    protected void initChannel(SocketChannel ch) throws Exception {
        if(EngineParams.isNettyLogging()){
            ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
        }
        ch.pipeline().addLast(new TcpHeaderDecoder());
        ch.pipeline().addLast(new RpcMsgDecoder());
        ch.pipeline().addLast(new TcpHeaderEncoder());
        ch.pipeline().addLast(new RpcMsgEncoder());

        ch.pipeline().addLast(new JsonPacketInboundHandler(msgTouchMethodDispatcher));
        ch.attr(Session.SECRRET_KEY).set(settings.secretKey);


    }
}
