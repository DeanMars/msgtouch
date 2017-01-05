package com.msgtouch.network.context;

/**
 * Created by Dean on 2017/1/4.
 */
public class Context {

    private static IBeanAccess beanAccess;

    public static void initBeanAccess(IBeanAccess beanAccess) {
        Context.beanAccess = beanAccess;
    }

    public static IBeanAccess getBeanAccess() {
        return beanAccess;
    }
}
