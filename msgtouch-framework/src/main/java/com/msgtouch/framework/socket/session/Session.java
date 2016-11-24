package com.msgtouch.framework.socket.session;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

/**
 * Created by Dean on 2016/9/22.
 */
public class Session {

    /**Session对象Key**/
    public static final AttributeKey<Session> SESSION_KEY=new AttributeKey<Session>("SESSION_KEY");
    /**秘钥*/
    public static final AttributeKey<String> SECRRET_KEY=new AttributeKey<String>("SECRRET_KEY");

    private String uid;

    private ChannelHandlerContext ctx;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public Session(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }
}
