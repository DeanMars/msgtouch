package com.msgtouch.framework.zookeeper;

import com.msgtouch.framework.context.Constraint;
import com.msgtouch.framework.settings.SettingsBuilder;
import com.msgtouch.framework.settings.ZookeeperSetting;
import com.msgtouch.framework.socket.SocketEngine;
import com.msgtouch.framework.socket.dispatcher.MsgTouchMethodDispatcher;
import com.msgtouch.framework.zookeeper.watcher.DefaultWatcher;
import com.msgtouch.framework.zookeeper.watcher.ServiceWatcher;
import com.msgtouch.framework.zookeeper.watcher.SessionWatcher;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Dean on 2016/9/6.
 */
public class ZooKeeperEngine {
    private static Logger logger= LoggerFactory.getLogger(SocketEngine.class);

    private ZooKeeper zooKeeper;
    private static final String key="zhangchuqiang";

    private static ZooKeeperEngine zooKeeperEngine=null;
    private ZooKeeperEngine(){}

    public static ZooKeeperEngine getInstances(){
        if(null==zooKeeperEngine){
            synchronized (ZooKeeperEngine.class){
                if(null==zooKeeperEngine) {
                    zooKeeperEngine = new ZooKeeperEngine();
                }
            }
        }
        return zooKeeperEngine;
    }


    public void bind(MsgTouchMethodDispatcher msgTouchMethodDispatcher){
        ZookeeperSetting setting= SettingsBuilder.buildZookeeperSetting();
        logger.info("ZooKeeperEngine bind hosts={},timeout={}", setting.connectString, setting.timeout);


    }

    public void bind(MsgTouchMethodDispatcher msgTouchMethodDispatcher, ZookeeperSetting setting){
        try {
            zooKeeper=initZooKeeper(setting);

            //创建根节点
          /*  SessionWatcher clusterWatcher=new SessionWatcher();
            String root="/"+ Constraint.ZOOKEEPER_ROOT;
            Stat stat=zooKeeper.exists(root,clusterWatcher);
            if(null==stat){
                zooKeeper.create(root, Constraint.ZOOKEEPER_ROOT.getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.PERSISTENT);
            }
            zooKeeper.getChildren(root,clusterWatcher);*/

            //创建根节点
            initMsgTouchRoot();

            //service节点
            initMsgTouchServiceRoot();

            //serssion节点
            initMsgTouchSessionRoot(new SessionWatcher());

           /* //创建当前服务临时节点
            ServiceWatcher serviceWatcher=new ServiceWatcher();
            InetAddress inetAddress=InetAddress.getLocalHost();
            String ip=inetAddress.getHostAddress().toString();
            clusterPath=root+"/"+ip+":"+setting.port;
            Stat serverStat=zooKeeper.exists(clusterPath,serviceWatcher);

            Set<String> set=msgTouchMethodDispatcher.getCmds();
            TouchCluster cluster=new TouchCluster();
            cluster.setClusterName(setting.clusterNames);
            cluster.setIp(ip);
            cluster.setPort(setting.port);
            cluster.setServices(set);

            String data= JSON.toJSONString(cluster);
            logger.info("ZkManager add server services data={}",data);
            if(null==serverStat){
                zooKeeper.create(clusterPath, data.getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.EPHEMERAL);
            }else{
                zooKeeper.setData(clusterPath, data.getBytes(),serverStat.getVersion());
            }*/


            logger.info("ZkManager bind success !");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void regesiteService(MsgTouchMethodDispatcher msgTouchMethodDispatcher){
        List<String> serviceList= msgTouchMethodDispatcher.getClusterlist();
        ServiceWatcher serviceWatcher=new ServiceWatcher();
        for(String serviceName:serviceList){

        }

    }


    private void freshServiceNode(String serviceName, ServiceWatcher serviceWatcher) throws Exception{
        if(StringUtils.isNotEmpty(serviceName)) {
            String root = "/" + Constraint.ZOOKEEPER_ROOT + "/" + Constraint.ZOOKEEPER_SERVICE_ROOT + "/" + serviceName;
            Stat stat=zooKeeper.exists(root,serviceWatcher);
            if(null==stat){
                zooKeeper.create(root, Constraint.ZOOKEEPER_ROOT.getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.PERSISTENT);
            }

        }
    }



    private  ZooKeeper initZooKeeper(ZookeeperSetting setting) throws IOException {
        if(null==zooKeeper) {
            zooKeeper = new ZooKeeper(setting.connectString, setting.timeout, new DefaultWatcher());
            zooKeeper.addAuthInfo("digest", key.getBytes());
        }
        return zooKeeper;
    }

    private void initMsgTouchRoot() throws Exception {
        String root="/"+ Constraint.ZOOKEEPER_ROOT;
        Stat stat=zooKeeper.exists(root,false);
        if(null==stat){
            zooKeeper.create(root, Constraint.ZOOKEEPER_ROOT.getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.PERSISTENT);
        }
    }

    private void initMsgTouchServiceRoot() throws Exception {
        String root="/"+ Constraint.ZOOKEEPER_ROOT+"/"+Constraint.ZOOKEEPER_SERVICE_ROOT;
        Stat stat=zooKeeper.exists(root,false);
        if(null==stat){
            zooKeeper.create(root, Constraint.ZOOKEEPER_ROOT.getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.PERSISTENT);
        }
    }

    private void initMsgTouchSessionRoot(Watcher watcher) throws Exception {
        String root="/"+ Constraint.ZOOKEEPER_ROOT+"/"+Constraint.ZOOKEEPER_SESSION_ROOT;
        Stat stat=zooKeeper.exists(root,watcher);
        if(null==stat){
            zooKeeper.create(root, Constraint.ZOOKEEPER_ROOT.getBytes(), ZooDefs.Ids.CREATOR_ALL_ACL, CreateMode.PERSISTENT);
        }
    }


    public  ZooKeeper getZooKeeper(){
        return zooKeeper;
    }

}
