package com.msgtouch.broker.route;

/**
 * Created by Dean on 2017/1/4.
 */
public class RouteTarget  implements Cloneable {
    private String appName;
    private String address;
    private int port;
    private long uid;
    private String gameId;
    private int size;

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

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {

        return super.clone();
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public static void main(String [] args){
        RouteTarget routeTarget=new RouteTarget();
        routeTarget.setAppName("3214324");
        routeTarget.setGameId("050200");
        routeTarget.setUid(123412312);
        routeTarget.setAddress("23443243");
        routeTarget.setPort(8090);

        try {
            RouteTarget routeTarget1=(RouteTarget)routeTarget.clone();

            System.out.println(routeTarget.getAppName());
            System.out.println(routeTarget1.getAppName());
            System.out.println(routeTarget1.getAppName()==routeTarget.getAppName());

            System.out.println(routeTarget.getGameId());
            System.out.println(routeTarget1.getGameId());
            System.out.println(routeTarget1.getGameId()==routeTarget.getGameId());

            System.out.println(routeTarget.getUid());
            System.out.println(routeTarget1.getUid());
            System.out.println(routeTarget1.getUid()==routeTarget.getUid());

            System.out.println(routeTarget.getAddress());
            System.out.println(routeTarget1.getAddress());
            System.out.println(routeTarget1.getAddress()==routeTarget.getAddress());

            System.out.println(routeTarget.getPort());
            System.out.println(routeTarget1.getPort());
            System.out.println(routeTarget1.getPort()==routeTarget.getPort());

        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }
}
