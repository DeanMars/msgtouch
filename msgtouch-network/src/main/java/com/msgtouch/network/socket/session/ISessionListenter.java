package com.msgtouch.network.socket.session;

/**
 * Created by Dean on 2017/1/16.
 */
public interface ISessionListenter {
    /**
     * session注册
     * @param session
     */
    void sessionRegistered(ISession session);

    /**
     * session 激活
     * @param session
     */
    void sessionActive(ISession session);

    /**
     * session 失效
     * @param session
     */
    void sessionInActive(ISession session);

}
