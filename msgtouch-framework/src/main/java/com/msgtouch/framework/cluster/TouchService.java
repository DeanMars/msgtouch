package com.msgtouch.framework.cluster;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dean on 2016/11/22.
 */
public class TouchService {
    private String host;
    private int port;
    private List<TouchApp> appList=new ArrayList<TouchApp>();

    public List<TouchApp> getAppList() {
        return appList;
    }

    public void setAppList(List<TouchApp> appList) {
        this.appList = appList;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
