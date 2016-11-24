package com.msgtouch.framework.socket.server;

import com.msgtouch.framework.socket.dispatcher.MsgTouchMethodDispatcher;
import com.msgtouch.framework.socket.handler.MsgTouchServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Created by Dean on 2016/9/7.
 */
public class SocketServerEngine {
    private Logger log= LoggerFactory.getLogger(SocketServerEngine.class);
    private static Properties properties;

    private SocketServerSetting settings;
    public SocketServerEngine(SocketServerSetting socketServerSetting) {
        this.settings=socketServerSetting;
    }


    /**
     * 启动网络服务
     * */
    public void bind(MsgTouchMethodDispatcher msgTouchMethodDispatcher){
        log.info("SocketServerEngine Init!");
        final EventLoopGroup bossGroup = new NioEventLoopGroup(settings.bossThreadSize);
        final EventLoopGroup workerGroup = new NioEventLoopGroup(settings.workerThreadSize);
        try {
            ChannelInitializer<SocketChannel> initializer=new MsgTouchServerInitializer(msgTouchMethodDispatcher);
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(initializer);
            ChannelFuture f =  b.bind(settings.port).sync();
            log.info("Boss thread : {}",settings.bossThreadSize);
            log.info("Worker thread : {}",settings.workerThreadSize);
            log.info("Logic thread:{}",settings.cmdThreadSize);
            log.info("Socket port :{}",settings.port);

            //f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
            if(log.isErrorEnabled()){
                log.error("<<<<<<<SocketServerEngine Start Error!>>>>>>", e);
            }
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            return;
        }
        log.info("SocketServerEngine Start OK!");
    }


}
