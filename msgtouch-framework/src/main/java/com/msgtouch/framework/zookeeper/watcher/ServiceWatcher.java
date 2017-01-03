/*
package com.msgtouch.framework.zookeeper.watcher;

import com.msgtouch.framework.zookeeper.ZooKeeperEngine;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

*/
/**
 * Created by Dean on 2016/9/12.
 *//*

public class ServiceWatcher implements Watcher {
    private Logger logger= LoggerFactory.getLogger(ServiceWatcher.class);

    public void process(WatchedEvent watchedEvent) {
        ZooKeeper zooKeeper= ZooKeeperEngine.getInstances().getZooKeeper();
        logger.info("ServiceWatcher process watchedEvent={} "+watchedEvent.toString());
        try {
            zooKeeper.exists(watchedEvent.getPath(),this);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



}
*/
