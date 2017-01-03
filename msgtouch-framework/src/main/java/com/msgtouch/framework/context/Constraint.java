package com.msgtouch.framework.context;

/**
 * Created by Dean on 2016/9/6.
 */
public class Constraint {

    /**
     * framework
     */
    public static final String FRAMEWORK_NAME="MsgTouch";
    //toucher 集群名
    public static final String MSGTOUCH_TOUCHER="msgToucher";
    //broker 集群名
    public static final String MSGTOUCH_BROKER="msgBroker";

    public static final String FRAMEWORK_SERVICE_KEY="MsgTouch_Services";
    public static final String FRAMEWORK_SESSION_KEY="MsgTouch_UserSession";
    /**
     * 配置
     */
    //应用名
    public static final String APP_NAME="app.name";
    //应用版本
    public static final String APP_VERSION="app.version";
    //应用扩展信息
    public static final String APP_EXT="app.ext";
    //本地ip
    public static final String LOCAL_IP="local.ip";

    /**
     * consul配置
     */
    //consul服务器host port
    public static final String CONSUL_HOSTANDPORT="consul.hostAndPort";
    //consul健康检查url
    public static final String CONSUL_HEALTHURL="consul.healthUrl";
    //consul健康检查时间间隔*
    public static final String CONSUL_HEALTHINTERVALSECOND="consul.healthIntervalSecond";


    /**
     * zookeeper配置
     */
    //zookeeper hosts
    public static final String ZOOKEEPER_CONNECTSTRING="zookeeper.address";
    //zookeeper sessiontimeout
    public static final String ZOOKEEPER_SESSIONTIMEOUT="zookeeper.sessiontimeout";
    //zookeeper MsgTouch
    public static final String ZOOKEEPER_ROOT="MsgTouch";
    //zookeeper MsgTouch service
    public static final String ZOOKEEPER_SERVICE_ROOT="services";
    //zookeeper MsgTouch session
    public static final String ZOOKEEPER_SESSION_ROOT="session";

    /**
     * netty配置
     */
    public static final String TCP_SERVER_PORT="tcp.server.port";
    //Netty层Worker类线程数
    public static final String TCP_SERVER_WORKERTHREADSIZE="tcp.server.workerThreadSize";
    //Message处理线程池大小
    public static final String TCP_SERVER_CMDTHREADSIZE ="tcp.server.cmdThreadSize";
    //Netty层Boss类线程数
    public static final String TCP_SERVER_BOSSTHREADSIZE="tcp.server.bossThreadSize";

}
