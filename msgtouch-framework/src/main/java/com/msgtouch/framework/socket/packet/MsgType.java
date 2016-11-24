package com.msgtouch.framework.socket.packet;

/**
 * Created by Dean on 2016/9/9.
 */
public enum MsgType {
    JSON(1),ProtoBuf(2), JavaSerialization(3),CustomParam(4);
    public int value;
    MsgType(int value){
        this.value=value;

    }
    public static MsgType valueOf(int value){
        switch (value){
            case 1:
                return JSON;
            case 2:
                return ProtoBuf;
            case 3:
                return JavaSerialization;
            case 4:
                return CustomParam;
            default:
                return JSON;
        }
    }
}

