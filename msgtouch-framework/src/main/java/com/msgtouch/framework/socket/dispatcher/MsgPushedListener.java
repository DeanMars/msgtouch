package com.msgtouch.framework.socket.dispatcher;

/**
 * Created by Dean on 2016/11/29.
 */
public interface MsgPushedListener<T> {

    T msgReceived(T t);

}
