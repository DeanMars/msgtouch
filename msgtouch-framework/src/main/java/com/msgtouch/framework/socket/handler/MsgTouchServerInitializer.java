/**
 * Channel初始化的自定义类，用来定义解码编码以及相关事件处理器
 * @author Chenlong
 * */
package com.msgtouch.framework.socket.handler;


import com.msgtouch.framework.socket.codec.BilingHeaderDecoder;
import com.msgtouch.framework.socket.codec.BilingHeaderEncoder;
import com.msgtouch.framework.socket.dispatcher.MsgTouchMethodDispatcher;
import com.msgtouch.framework.socket.codec.BilingMsgDecoder;
import com.msgtouch.framework.socket.codec.BilingMsgEncoder;
import com.msgtouch.framework.utils.EngineParams;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

@Sharable
public class MsgTouchServerInitializer extends ChannelInitializer<SocketChannel>{

	private MsgTouchMethodDispatcher msgTouchMethodDispatcher;

	public MsgTouchServerInitializer(MsgTouchMethodDispatcher msgTouchMethodDispatcher) {
		this.msgTouchMethodDispatcher = msgTouchMethodDispatcher;
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		if(EngineParams.isNettyLogging()){
			ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
		}

		ch.pipeline().addLast(new BilingHeaderDecoder());
		ch.pipeline().addLast(new BilingMsgDecoder());
		ch.pipeline().addLast(new BilingHeaderEncoder());
		ch.pipeline().addLast(new BilingMsgEncoder());


		ch.pipeline().addLast(new MsgTouchInboundHandler(msgTouchMethodDispatcher));


	}

}
