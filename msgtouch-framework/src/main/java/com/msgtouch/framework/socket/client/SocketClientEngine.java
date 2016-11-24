package com.msgtouch.framework.socket.client;

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

    private SocketClientSetting settings;
    private EventLoopGroup eventExecutors;
    private Channel channel;
    private Bootstrap bootstrap;

    public SocketClientEngine(SocketClientSetting socketClientSetting) {
        this.settings=socketClientSetting;
    }

    public void connect() throws Exception{
        this.connect(settings.host, settings.port);
    }

    public void connect(String host, int port) throws Exception{
        log.info("SocketClientEngine connect to {}:{}",host,port);
        ChannelFuture f =this.bootstrap.connect(host,port);
        ChannelFuture future=f.sync();
        future.get();
        this.channel=f.channel();

    }

    public void startSocket() throws Exception{
        EventLoopGroup workerGroup=null;
        if(this.eventExecutors==null){
            workerGroup= new NioEventLoopGroup(settings.workerThreadSize);
        }else{
            workerGroup=this.eventExecutors;
        }
        ChannelInitializer<SocketChannel> initializer=new SocketClientInitializer(settings);
        this.bootstrap= new Bootstrap();
        this.bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(initializer);
        connect();
        log.debug("Worker thread : {}",settings.workerThreadSize);
        log.debug("Logic thread:{}",settings.cmdThreadSize);
        log.info("SocketClientEngine connect to {} success!",settings.host+":"+settings.port);
    }
    public Channel getChannel(){
        return this.channel;
    }

    public Session getSession(){
        return channel.attr(Session.SESSION_KEY).get();
    }
}
