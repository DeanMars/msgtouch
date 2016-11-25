package com.msgtouch.framework.settings;

/**
 * Created by Dean on 2016/9/7.
 */
public class SocketServerSetting {
    /**TCP端口**/
    public int port=8001;
    /**Netty层Worker类线程数*/
    public int workerThreadSize=10;
    /**Message处理线程池大小**/
    public int cmdThreadSize =10;
    /**Netty层Boss类线程数**/
    public int bossThreadSize=2;

}
