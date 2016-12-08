package com.msgtouch.framework.socket.codec;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Dean on 2016/9/8.
 */
@ChannelHandler.Sharable
public class MsgDecoder<T> extends MessageToMessageDecoder<T>{
    private static Logger logger= LoggerFactory.getLogger(MsgDecoder.class);
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        logger.error("MsgDecoder Exception Caught {}",ctx.channel(),cause);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, T t, List<Object> list) throws Exception {

    }
}
