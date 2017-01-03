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

public class SessionWatcher implements Watcher {
    private Logger logger= LoggerFactory.getLogger(SessionWatcher.class);

    public void process(WatchedEvent watchedEvent) {
        ZooKeeper zooKeeper= ZooKeeperEngine.getInstances().getZooKeeper();
        logger.info("SessionWatcher process watchedEvent={} "+watchedEvent.toString());
        try {
            zooKeeper.getChildren(watchedEvent.getPath(),this);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
*/
