package com.msgtouch.framework.socket.client;

import com.msgtouch.framework.settings.SocketClientSetting;
import com.msgtouch.framework.socket.codec.MsgDecoder;
import com.msgtouch.framework.socket.codec.MsgEncoder;
import com.msgtouch.framework.socket.dispatcher.MethodDispatcher;
import com.msgtouch.framework.socket.handler.MsgTouchClientInitializer;
import com.msgtouch.framework.socket.session.ISession;
import com.msgtouch.framework.socket.session.Session;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Dean on 2016/9/22.
 */
public class SocketClientEngine {
    private static final Logger log= LoggerFactory.getLogger(SocketClientEngine.class);
    private MethodDispatcher msgTouchMethodDispatcher;
    private SocketClientSetting settings;
    private EventLoopGroup eventExecutors;
    private Channel channel;
    private Bootstrap bootstrap;

    public SocketClientEngine(SocketClientSetting socketClientSetting,MethodDispatcher msgTouchMethodDispatcher) {
        this.settings=socketClientSetting;
        this.msgTouchMethodDispatcher=msgTouchMethodDispatcher;
    }


    public void bind(MsgDecoder msgTouchDecoder, MsgEncoder msgTouchEncoder) throws Exception{
        EventLoopGroup workerGroup=null;
        if(this.eventExecutors==null){
            workerGroup= new NioEventLoopGroup(settings.workerThreadSize);
        }else{
            workerGroup=this.eventExecutors;
        }
        ChannelInitializer<SocketChannel> initializer=new MsgTouchClientInitializer(settings,msgTouchMethodDispatcher,
                msgTouchDecoder,msgTouchEncoder);
        this.bootstrap= new Bootstrap();
        this.bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(initializer);
        log.debug("Worker thread : {}",settings.workerThreadSize);
        log.debug("Logic thread:{}",settings.cmdThreadSize);
        log.info("MsgTouchSocketClientEngine connect to {} success!",settings.host+":"+settings.port);

        log.info("SocketClientEngine connect to {}:{}",settings.host,settings.port);
        ChannelFuture f =this.bootstrap.connect(settings.host,settings.port);
        ChannelFuture future=f.sync();
        future.get();
        this.channel=f.channel();

    }
    public Channel getChannel(){
        return this.channel;
    }

    public ISession getSession(){
        return channel.attr(Session.SESSION_KEY).get();
    }

    public SocketClientSetting getSettings() {
        return settings;
    }

    public MethodDispatcher getMsgTouchMethodDispatcher() {
        return msgTouchMethodDispatcher;
    }
}
