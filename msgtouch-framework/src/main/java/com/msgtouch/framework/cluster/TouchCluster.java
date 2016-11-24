package com.msgtouch.framework.cluster;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Dean on 2016/9/12.
 */
public class TouchCluster {
    private String ip;
    private int port;
    private String version;
    private Set<String> cmds=new HashSet<String>();


    private String ext;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Set<String> getCmds() {
        return cmds;
    }

    public void setCmds(Set<String> cmds) {
        this.cmds = cmds;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void addCmd(String service){
        cmds.add(service);
    }

}
