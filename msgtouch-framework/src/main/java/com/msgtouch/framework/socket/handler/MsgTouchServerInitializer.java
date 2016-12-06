/**
 * Channel初始化的自定义类，用来定义解码编码以及相关事件处理器
 * @author Chenlong
 * */
package com.msgtouch.framework.socket.handler;


import com.msgtouch.framework.socket.codec.TcpHeaderDecoder;
import com.msgtouch.framework.socket.codec.TcpHeaderEncoder;
import com.msgtouch.framework.socket.dispatcher.JsonPacketMethodDispatcher;
import com.msgtouch.framework.socket.codec.RpcMsgDecoder;
import com.msgtouch.framework.socket.codec.RpcMsgEncoder;
import com.msgtouch.framework.utils.EngineParams;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

@Sharable
public class MsgTouchServerInitializer extends ChannelInitializer<SocketChannel>{

	private JsonPacketMethodDispatcher msgTouchMethodDispatcher;

	public MsgTouchServerInitializer(JsonPacketMethodDispatcher msgTouchMethodDispatcher) {
		this.msgTouchMethodDispatcher = msgTouchMethodDispatcher;
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		if(EngineParams.isNettyLogging()){
			ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
		}

		ch.pipeline().addLast(new TcpHeaderDecoder());
		ch.pipeline().addLast(new RpcMsgDecoder());
		ch.pipeline().addLast(new TcpHeaderEncoder());
		ch.pipeline().addLast(new RpcMsgEncoder());


		ch.pipeline().addLast(new JsonPacketInboundHandler(msgTouchMethodDispatcher));


	}

}
