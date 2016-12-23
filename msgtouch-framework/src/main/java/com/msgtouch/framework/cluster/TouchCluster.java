package com.msgtouch.framework.cluster;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dean on 2016/12/23.
 */
public class TouchCluster {
    private String appName;
    private List<TouchService> services=new ArrayList<TouchService>();

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public List<TouchService> getServices() {
        return services;
    }

    public void setServices(List<TouchService> services) {
        this.services = services;
    }

    public void addService(TouchService services){
        this.services.add(services);
    }
}
