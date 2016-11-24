package com.msgtouch.framework.cluster;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dean on 2016/11/22.
 */
public class TouchService {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private List<TouchCluster>  providers=new ArrayList<TouchCluster>();

    private List<TouchCluster>  consumers=new ArrayList<TouchCluster>();

    private List<String>  routers=new ArrayList<String>();

    public List<TouchCluster> getProviders() {
        return providers;
    }

    public void setProviders(List<TouchCluster> providers) {
        this.providers = providers;
    }

    public List<TouchCluster> getConsumers() {
        return consumers;
    }

    public void setConsumers(List<TouchCluster> consumers) {
        this.consumers = consumers;
    }

    public List<String> getRouters() {
        return routers;
    }

    public void setRouters(List<String> routers) {
        this.routers = routers;
    }

    public void addProvider(TouchCluster touchCluster){
        if(null!=touchCluster){
            providers.add(touchCluster);
        }
    }

    public void addConsumer(TouchCluster touchCluster){
        if(null!=touchCluster){
            consumers.add(touchCluster);
        }
    }

    public void addRouters(String router){
        if(null!=router){
            routers.add(router);
        }
    }


}
