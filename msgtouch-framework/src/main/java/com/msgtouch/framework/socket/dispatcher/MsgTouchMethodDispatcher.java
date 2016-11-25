package com.msgtouch.framework.socket.dispatcher;

import com.msgtouch.framework.socket.packet.MsgPacket;
import com.msgtouch.framework.socket.session.ISession;
import com.msgtouch.framework.socket.session.Session;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by Dean on 2016/9/8.
 */
public class MsgTouchMethodDispatcher {
    private static Logger logger= LoggerFactory.getLogger(MsgTouchMethodDispatcher.class);

    private Map<String,MsgTouchMethodInvoker> methodInvokerMap =new HashMap<String,MsgTouchMethodInvoker>();
    private List<String> servicelist=new ArrayList<String>();

    public MsgTouchMethodDispatcher(){}

    public void addMethod(String cmd,MsgTouchMethodInvoker invoker){
        if(methodInvokerMap.containsKey(cmd)){
            logger.warn("RpcService method has exists:cmd={}",cmd);
            return;
        }
        methodInvokerMap.put(cmd, invoker);
    }

    public void dispatcher(ISession session, MsgPacket msgPacket){
        if(msgPacket.isCall()){
            String cmd=msgPacket.getCmd();
            MsgTouchMethodInvoker msgTouchMethodInvoker=methodInvokerMap.get(cmd);
            if(null==msgTouchMethodInvoker){
                throw new RuntimeException("MsgTouchMethodDispatcher method cmd="+cmd+" not found!");
            }
            Object[] params = msgPacket.getParams();
            if(null!=params) {
                try {
                    Object ret=msgTouchMethodInvoker.invoke(params);
                    msgPacket.setParams(new Object[]{ret});
                    msgPacket.setCall(false);
                    Channel channel=session.getChannel();
                    if(channel.isActive()) {
                        channel.writeAndFlush(msgPacket);
                    }else{
                        logger.error("channel is not active:packet = {}",msgPacket);
                    }
                } catch (Exception e) {
                    logger.info("MsgTouchMethodDispatcher invoke method exception ！！");
                    e.printStackTrace();
                }

            }
        }else{
            SyncRpcCallBack callBack=session.getAttribute(Session.SYNC_CALLBACK_MAP).get(msgPacket.getUuid());
            if(callBack!=null){
                if(msgPacket.getParams().length==1){
                    callBack.processResult(session,msgPacket.getParams()[0]);
                }else{
                    callBack.processResult(session, msgPacket.getParams());
                }
            }
        }

    }


    public Set<String> getCmds(){
        return methodInvokerMap.keySet();
    }

    public void addServiceClass(String service){
        if(!servicelist.contains(service)){
            servicelist.add(service);
        }
    }

    public List<String> getServicelist() {
        return servicelist;
    }
}
