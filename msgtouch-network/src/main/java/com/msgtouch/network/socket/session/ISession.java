/**
 * 封装的ISession接口，一个客户端连接对应一个Session
 * @author Chenlong
 * */
package com.msgtouch.network.socket.session;

import com.msgtouch.network.socket.packet.MsgPBPacket;
import com.msgtouch.network.socket.packet.MsgPacket;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public interface ISession {
	/**
	 * @return Session创建时间
	 * */
	long getCreateTime();
	/**
	 * @return 得到SocketChannel
	 * */
	Channel getChannel();
	/**
	 * @return 得到客户端远程IP地址
	 * */
	String getClientAddress();

	String getClientHost();
	/**
	 * @return 客户端远程端口
	 * */
	int getClientPort();
	/**
	 * 断开客户端连接
	 * @param immediately 是否立即断开
	 * */
	Future<?> disconnect(boolean immediately);

	Future<?> disconnect(boolean immediately, MsgPacket packet);
	/**
	 * @return session最后活动时间
	 * */
	long getLastActiveTime();
	/**
	 * @param lastActiveTime Session最后活动时间
	 * */
	void setLastActiveTime(long lastActiveTime);
	/**
	 * @return 是否活动状态
	 * */
	boolean isActive();
    /**
     * @param  active 是否活动状态
     * */
	void setActive(boolean active);
	/**
	 * 设置一个变量到Session中保存，Session断开连接后会销毁
	 * 此方法是线程安全的
     * @param key key
     * @param value value
	 * */
	<T> void setAttribute(AttributeKey<T> key, T value);
	/**
     * @param key key
	 * @return 是否包含指定变量
	 * */
	boolean containsAttribute(AttributeKey<?> key);
	/**
     * @param key key
	 * @return 获取指定变量
	 * */
	<T> T getAttribute(AttributeKey<T> key);
	/**
	 * 移除指定变量
     * @param key
	 * */
	<T> void removeAttribute(AttributeKey<T> key);
	/**
	 * 清除Session相关信息
	 * */
	void clear();
	Future<?> writeAndFlush(MsgPacket packet);
	//void asyncRpcSend(MsgPacket packet, RpcCallback callback) ;
	/**
	 * 发送同步消息，并等待结果返回
	 * @return 返回结果
	 * */
	<T> T syncRpcSend(MsgPacket packet, Class<T> resultType, long timeout, TimeUnit unit)throws InterruptedException,ExecutionException, TimeoutException;

	MsgPBPacket.Packet.Builder syncRpcSend(MsgPBPacket.Packet.Builder packet, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException;

	<T> T pushJsonMsg(T t, long timeoutSecond) throws InterruptedException, ExecutionException, TimeoutException ;

	MsgPBPacket.Packet.Builder pushPBMsg(MsgPBPacket.Packet.Builder packet, long timeoutSecond) throws InterruptedException, ExecutionException, TimeoutException ;
}
