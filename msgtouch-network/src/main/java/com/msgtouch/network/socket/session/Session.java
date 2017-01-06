package com.msgtouch.network.socket.session;


import com.msgtouch.network.socket.dispatcher.ASyncRpcCallBack;
import com.msgtouch.network.socket.dispatcher.RpcCallBack;
import com.msgtouch.network.socket.dispatcher.SyncRpcCallBack;
import com.msgtouch.network.socket.packet.MsgPBPacket;
import com.msgtouch.network.socket.packet.MsgPacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.DefaultProgressivePromise;
import io.netty.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

public class Session implements ISession{
	/**Session对象Key**/
	public static final AttributeKey<ISession> SESSION_KEY=new AttributeKey<ISession>("SESSION_KEY");
	/**秘钥*/
	public static final AttributeKey<String> SECRRET_KEY=new AttributeKey<String>("SECRRET_KEY");
	/**秘钥*/
	public static final AttributeKey<String> USER_KEY=new AttributeKey<String>("USER_KEY");

	/**同步返回Future*/
	public static final AttributeKey<Map<String,SyncRpcCallBack<?>>> SYNC_CALLBACK_MAP =new AttributeKey<Map<String,SyncRpcCallBack<?>>>("SYNC_CALLBACK_MAP");
	/**异步回调Map*/
	public static final AttributeKey<Map<String,RpcCallBack>> ASYNC_CALLBACK_MAP=new AttributeKey<Map<String,RpcCallBack>>("ASYNC_CALLBACK_MAP");

	public static final  RpcCallBack DEFAULT_ASYNC_CALL_BACK=new ASyncRpcCallBack();

	private Channel channel;//连接通道
	private long createTime;//创建时间
	private long lastActiveTime;//最后活动时间
	private volatile boolean active=true;
	//Netty已经触发过inActive
	private AtomicBoolean nettyInActive=new AtomicBoolean(false);
	private static Logger logger= LoggerFactory.getLogger(Session.class);
	public Session(Channel channel){
		this.channel=channel;
		this.createTime= System.currentTimeMillis();
	}

	public long getCreateTime() {
		// TODO Auto-generated method stub
		return this.createTime;
	}

	public Channel getChannel() {
		return channel;
	}

	public String getClientAddress() {
		// TODO Auto-generated method stub
		return channel.remoteAddress().toString();
	}
	public String getClientHost(){
		return channel.remoteAddress().toString().split(":")[0].replaceAll("/","");
	}

	public int getClientPort() {
		// TODO Auto-generated method stub
		return Integer.valueOf(channel.remoteAddress().toString().split(":")[1]).intValue();
	}

	public Future<?> disconnect(boolean immediately) {
		ChannelFuture future=null;
		try{
			if(!nettyInActive.get()){
				nettyInActive.compareAndSet(false,true);
				future=channel.disconnect();
				if(immediately){
					future.sync();
				}
			}
		}catch (Exception e){
			e.printStackTrace();
			logger.error("Disconnect error",e);
		}

		return future;
	}

	public long getLastActiveTime() {
		// TODO Auto-generated method stub
		return lastActiveTime;
	}

	public void setLastActiveTime(long lastActiveTime) {
		// TODO Auto-generated method stub
		this.lastActiveTime=lastActiveTime;
	}

	public <T>Future<?> writeAndFlush(T t) {
		if(t==null){
			throw new NullPointerException("Session writeAndFlush packet can not be null");
		}
		if(channel.isActive()){
			ChannelFuture future=channel.writeAndFlush(t);
			setLastActiveTime(System.currentTimeMillis());
			return future;
		}else{
			logger.error("Session is not active:packet = {}",t);
			return channel.newSucceededFuture();
		}
	}

	public boolean containsAttribute(AttributeKey<?> key) {
		// TODO Auto-generated method stub
		return channel.attr(key).get()!=null;
	}

	public <T> T getAttribute(AttributeKey<T> key) {
		// TODO Auto-generated method stub
		return channel.attr(key).get();
	}

	public void clear() {

	}

	public boolean isActive() {
		// TODO Auto-generated method stub
		return channel.isActive()&&active;
	}

	public void setActive(boolean active) {
		// TODO Auto-generated method stub
		this.active=active;
	}

	public Future<?> disconnect(boolean immediately, MsgPacket packet) {
		Future<?> future=channel.newSucceededFuture();
		if(channel.isActive()){
			future=this.writeAndFlush(packet);
			try {
				if(immediately){
					future.sync();
				}
				future=disconnect(immediately);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				logger.error("Wait disconnect task error",e);
			}
		}
		return future;
	}

	public <T> void removeAttribute(AttributeKey<T> key) {
		channel.attr(key).remove();
	}


	public <T> void setAttribute(AttributeKey<T> key, T value) {
		channel.attr(key).set(value);
	}


	public <T>T syncRpcSend(MsgPacket packet, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		packet.setUuid(UUID.randomUUID().toString());
		packet.setCall(true);
		String uuid=packet.getUuid();
		try {
			DefaultProgressivePromise<T> syncPromise=new DefaultProgressivePromise<T>(channel.eventLoop());
			SyncRpcCallBack callBack=new SyncRpcCallBack(syncPromise);
			Map<String,SyncRpcCallBack<?>> callBackMap=getAttribute(SYNC_CALLBACK_MAP);
			callBackMap.put(uuid, callBack);
			writeAndFlush(packet);
			T result=syncPromise.get(timeout, TimeUnit.SECONDS);
			callBackMap.remove(uuid);
			return result;
		}finally {
			getAttribute(SYNC_CALLBACK_MAP).remove(uuid);
		}
	}

	public MsgPBPacket.Packet.Builder syncRpcSend(MsgPBPacket.Packet.Builder packet, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		packet.setSeq(UUID.randomUUID().toString());
		packet.setMsgType(MsgPBPacket.MsgType.Request);
		String uuid=packet.getSeq();
		MsgPBPacket.Packet.Builder  result=null;
		try {
			result=giveDefaultProgressivePromise(uuid,packet,timeout,unit);
		}finally {
			getAttribute(SYNC_CALLBACK_MAP).remove(uuid);
		}
		return result;
	}

	public <T>T syncPushJsonMsg(T t, long timeout,TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		MsgPacket packet=new MsgPacket("",new Object[]{t});
		return syncRpcSend(packet,timeout,unit);
	}

	public MsgPBPacket.Packet.Builder  syncPushPBMsg(MsgPBPacket.Packet.Builder packet, long timeoutSecond,TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		packet.setRetCode(MsgPBPacket.RetCode.PUSH);
		return syncRpcSend(packet,timeoutSecond,unit);
	}

	public void  asyncPushPBMsg(MsgPBPacket.Packet.Builder packet,RpcCallBack rpcCallback) throws InterruptedException, ExecutionException, TimeoutException {
		packet.setSeq(UUID.randomUUID().toString());
		packet.setMsgType(MsgPBPacket.MsgType.Request);
		packet.setRetCode(MsgPBPacket.RetCode.PUSH);
		String uuid=packet.getSeq();
		if(rpcCallback==null){
			rpcCallback=DEFAULT_ASYNC_CALL_BACK;
		}
		Map<String,RpcCallBack> callBackMap=getAttribute(ASYNC_CALLBACK_MAP);
		callBackMap.put(uuid, rpcCallback);
		writeAndFlush(packet);
	}

	public void asyncRpcSend(MsgPacket packet,RpcCallBack rpcCallback){
		String uuid= UUID.randomUUID().toString();
		packet.setUuid(uuid);
		if(rpcCallback==null){
			rpcCallback=DEFAULT_ASYNC_CALL_BACK;
		}
		Map<String,RpcCallBack> callBackMap=getAttribute(ASYNC_CALLBACK_MAP);
		callBackMap.put(uuid, rpcCallback);
		writeAndFlush(packet);
	}

	public void asyncRpcSend(MsgPBPacket.Packet.Builder packet,RpcCallBack rpcCallback){
		packet.setSeq(UUID.randomUUID().toString());
		packet.setMsgType(MsgPBPacket.MsgType.Request);
		String uuid=packet.getSeq();
		if(rpcCallback==null){
			rpcCallback=DEFAULT_ASYNC_CALL_BACK;
		}
		Map<String,RpcCallBack> callBackMap=getAttribute(ASYNC_CALLBACK_MAP);
		callBackMap.put(uuid, rpcCallback);
		writeAndFlush(packet);
	}

	private <T>T giveDefaultProgressivePromise(String seqId,T t,long timeout,TimeUnit unit)throws InterruptedException, ExecutionException, TimeoutException{
		DefaultProgressivePromise<T> syncPromise=new DefaultProgressivePromise<T>(channel.eventLoop());
		SyncRpcCallBack callBack=new SyncRpcCallBack(syncPromise);
		Map<String,SyncRpcCallBack<?>> callBackMap=getAttribute(SYNC_CALLBACK_MAP);
		callBackMap.put(seqId, callBack);
		writeAndFlush(t);
		T result=syncPromise.get(timeout, unit);
		callBackMap.remove(seqId);
		return result;
	}


}
