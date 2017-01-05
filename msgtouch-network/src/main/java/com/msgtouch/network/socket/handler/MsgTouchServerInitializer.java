/**
 * Channel初始化的自定义类，用来定义解码编码以及相关事件处理器
 * @author Chenlong
 * */
package com.msgtouch.network.socket.handler;


import com.msgtouch.network.socket.codec.MsgEncoder;
import com.msgtouch.network.socket.codec.TcpHeaderDecoder;
import com.msgtouch.network.socket.dispatcher.MethodDispatcher;
import com.msgtouch.network.socket.codec.MsgDecoder;
import com.msgtouch.network.socket.codec.TcpHeaderEncoder;
import com.msgtouch.network.utils.EngineParams;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

@Sharable
public class MsgTouchServerInitializer extends ChannelInitializer<SocketChannel>{
	private MsgDecoder msgTouchDecoder;
	private MsgEncoder msgTouchEncoder;
	private MethodDispatcher msgTouchMethodDispatcher;

	public MsgTouchServerInitializer(MethodDispatcher msgTouchMethodDispatcher,
									 MsgDecoder msgTouchDecoder, MsgEncoder msgTouchEncoder) {
		this.msgTouchMethodDispatcher = msgTouchMethodDispatcher;
		this.msgTouchDecoder=msgTouchDecoder;
		this.msgTouchEncoder=msgTouchEncoder;
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		if(EngineParams.isNettyLogging()){
			ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
		}

		ch.pipeline().addLast(new TcpHeaderDecoder());
		ch.pipeline().addLast(msgTouchDecoder);
		ch.pipeline().addLast(new TcpHeaderEncoder());
		ch.pipeline().addLast(msgTouchEncoder);


		ch.pipeline().addLast(new MsgTouchInboundHandler(msgTouchMethodDispatcher));


	}

}
