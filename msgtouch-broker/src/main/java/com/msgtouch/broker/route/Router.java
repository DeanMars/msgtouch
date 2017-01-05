package com.msgtouch.broker.route;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dean on 2017/1/4.
 */
public class Router {
    private String appName;
    private String address;
    private int port;
    private List<RouteTag> routeTags=new ArrayList<RouteTag>();

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public List<RouteTag> getRouteTags() {
        return routeTags;
    }

    public void setRouteTags(List<RouteTag> routeTags) {
        this.routeTags = routeTags;
    }

    public void addRouteTag(RouteTag routeTag){
        routeTags.add(routeTag);
    }

}
