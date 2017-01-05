package com.msgtouch.network.context;

/**
 * Created by Dean on 2017/1/4.
 */
public interface IBeanAccess {

    <T> T getBean(Class<T> clazz) throws Exception;

}
