package com.msgtouch.network.socket.packet;

import java.util.Arrays;

/**
 * Created by Dean on 2016/9/8.
 */
public class MsgPacket {
    private boolean isCall;
    private String uuid;
    private String cmd;
    private MsgType msgType=MsgType.JSON;
    private Object[] params;
    public MsgPacket(String cmd, Object[] params){
        this.cmd=cmd;
        this.params=params;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public MsgType getMsgType() {
        return msgType;
    }

    public void setMsgType(MsgType msgType) {
        this.msgType = msgType;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public boolean isCall() {
        return isCall;
    }

    public void setCall(boolean call) {
        isCall = call;
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder("RpcPacket{");
        sb.append(", cmd=").append(cmd);
        sb.append(", uuid='").append(uuid).append('\'');
        sb.append(", params=").append(Arrays.toString(params));
        sb.append('}');
        return sb.toString();
    }
}
