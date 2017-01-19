package com.msgtouch.network.socket.client;

import com.msgtouch.network.settings.SocketClientSetting;
import com.msgtouch.network.socket.codec.MsgDecoder;
import com.msgtouch.network.socket.codec.MsgEncoder;
import com.msgtouch.network.socket.dispatcher.MethodDispatcher;
import com.msgtouch.network.socket.handler.MsgTouchClientInitializer;
import com.msgtouch.network.socket.session.ISession;
import com.msgtouch.network.socket.session.ISessionListenter;
import com.msgtouch.network.socket.session.Session;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Dean on 2016/9/22.
 */
public class SocketClientEngine {
    private static final Logger log= LoggerFactory.getLogger(SocketClientEngine.class);
    private MethodDispatcher msgTouchMethodDispatcher;
    private SocketClientSetting settings;
    private Channel channel;
    private Bootstrap bootstrap;
    private ISessionListenter sessionListenter;
    private ChannelInitializer<SocketChannel> initializer;
    private NioEventLoopGroup nioEventLoopGroup;
    private AtomicInteger reconnectCount=new AtomicInteger();

    public SocketClientEngine(SocketClientSetting socketClientSetting, MethodDispatcher msgTouchMethodDispatcher) {
        this.settings=socketClientSetting;
        this.msgTouchMethodDispatcher=msgTouchMethodDispatcher;
    }

    public void bind(MsgDecoder msgTouchDecoder, MsgEncoder msgTouchEncoder) throws Exception{
        initializer=new MsgTouchClientInitializer(settings,msgTouchMethodDispatcher, msgTouchDecoder,msgTouchEncoder);
        ChannelFuture f=doConnect();
        this.channel=f.channel();
        channel.config().setAllocator(PooledByteBufAllocator.DEFAULT);
    }

    public ChannelFuture doConnect(){
        this.bootstrap= new Bootstrap();
        nioEventLoopGroup=new NioEventLoopGroup(settings.workerThreadSize);
        this.bootstrap.group(nioEventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(initializer);
        log.debug("Worker thread : {}",settings.workerThreadSize);
        log.debug("Logic thread:{}",settings.cmdThreadSize);
        final ChannelFuture f =this.bootstrap.connect(settings.host,settings.port);
        f.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if(channelFuture.isSuccess()){
                    log.info("SocketClientEngine connect to {} success!",settings.host+":"+settings.port);
                    runHeartBeats(settings.heartBeatsTime,settings.heartBeatsTimeUnit);
                }else{
                /*    f.channel().eventLoop().schedule(new Runnable() {
                        public void run() {
                            int count=reconnectCount.get();
                            log.info("SocketClientEngine reconnect to {} times={}!",settings.host+":"+settings.port,count);
                            if(count<=2){
                                reconnectCount.incrementAndGet();
                                doConnect();
                            }else{
                                sessionListenter.sessionInActive(null);
                            }
                        }
                    }, 3, TimeUnit.SECONDS);*/
                    throw new RuntimeException("doConnect overtime !!! ");
                    //sessionListenter.sessionInActive(null);
                }
            }
        });
        try {
            f.sync();
            f.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.channel=f.channel();
        return f;
    }

    public void shutdown(boolean immediately){
        try {
            if(channel!=null) {
                if (immediately) {
                    channel.close().sync();
                } else {
                    channel.close();
                }
            }
            if(null!=nioEventLoopGroup){
                if (immediately) {
                    nioEventLoopGroup.shutdownGracefully().sync();
                } else {
                    nioEventLoopGroup.shutdownGracefully();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void setSessionListenter(ISessionListenter sessionListenter){
        this.sessionListenter=sessionListenter;
        getSession().setSessionLisenter(sessionListenter);
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


    private void runHeartBeats(final int time, final TimeUnit timeUnit){
        ISession session=getSession();
        channel.eventLoop().scheduleAtFixedRate(new Runnable() {
            public void run() {
                getSession().heartBeats(time,timeUnit);
            }
        },0,time,timeUnit);
    }
}
