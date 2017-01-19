/**
 * 封装的ISession接口，一个客户端连接对应一个Session
 * @author Chenlong
 * */
package com.msgtouch.network.socket.session;

import com.msgtouch.network.socket.dispatcher.RpcCallBack;
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

	ISessionListenter getSessionLisenter();

	void setSessionLisenter(ISessionListenter sessionLisenter);


	<T>Future<?> writeAndFlush(T t);

	/**
	 * 心跳
	 */
	void heartBeats(int time,TimeUnit timeUnit) ;

	void cancelHeartBeats();
	/**
	 * 发送异步消息
	 * */
	void asyncRpcSend(MsgPacket packet, RpcCallBack callback) ;
	/**
	 * 发送异步消息
	 * */
	void asyncRpcSend(MsgPBPacket.Packet.Builder packet,RpcCallBack rpcCallback);
	/**
	 * 发送同步消息，并等待结果返回
	 * @return 返回结果
	 * */
	<T>T syncRpcSend(MsgPacket packet, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException;
	/**
	 * 发送同步消息，并等待结果返回
	 * @return 返回结果
	 * */
	MsgPBPacket.Packet.Builder syncRpcSend(MsgPBPacket.Packet.Builder packet, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException;

	/**
	 * 同步推送消息，并返回结果
	 * @param t
	 * @param timeout
	 * @param unit
	 * @param <T>
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	<T>T syncPushJsonMsg(T t, long timeout,TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException;

	/**
	 * 同步推送消息，并返回结果
	 * @param packet
	 * @param timeoutSecond
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	MsgPBPacket.Packet.Builder  syncPushPBMsg(MsgPBPacket.Packet.Builder packet, long timeoutSecond,TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException;

	/**
	 * 异步推送消息，并返回结果
	 * @param packet
	 * @param rpcCallback
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	void  asyncPushPBMsg(MsgPBPacket.Packet.Builder packet,RpcCallBack rpcCallback) throws InterruptedException, ExecutionException, TimeoutException;

	int getHeartBeatTime();

	void setHeartBeatTime(int heartBeatTime);

	TimeUnit getHeartBeatTimeUnit();

	void setHeartBeatTimeUnit(TimeUnit heartBeatTimeUnit);
}
