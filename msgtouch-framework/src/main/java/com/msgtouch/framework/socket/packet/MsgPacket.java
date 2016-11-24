package com.msgtouch.framework.socket.packet;

/**
 * Created by Dean on 2016/9/8.
 */
public class MsgPacket {
    private boolean isCall;
    private String packageId;
    private String gameId;
    private String avdNo;
    private int  uid=0;
    private String sdkVersion;
    private String cmd;
    private MsgType msgType= MsgType.JSON;
    private boolean fromCilent=false;
    private String params;
    public MsgPacket(String cmd, String params){
        this.cmd=cmd;
        this.params=params;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getAvdNo() {
        return avdNo;
    }

    public void setAvdNo(String avdNo) {
        this.avdNo = avdNo;
    }

    public boolean isFromCilent() {
        return fromCilent;
    }

    public String getSdkVersion() {
        return sdkVersion;
    }

    public void setSdkVersion(String sdkVersion) {
        this.sdkVersion = sdkVersion;
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

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public boolean getFromCilent() {
        return fromCilent;
    }

    public void setFromCilent(boolean fromCilent) {
        this.fromCilent = fromCilent;
    }

    public boolean isCall() {
        return isCall;
    }

    public void setCall(boolean call) {
        isCall = call;
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder("RpcPacket{");
        sb.append("fromCilent=").append(fromCilent);
        sb.append(", cmd=").append(cmd);
        sb.append(", uuid='").append(packageId).append('\'');
        //sb.append(", params=").append(Arrays.toString(params));
        sb.append('}');
        return sb.toString();
    }
}
