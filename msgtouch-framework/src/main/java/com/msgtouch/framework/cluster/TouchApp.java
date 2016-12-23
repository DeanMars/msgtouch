package com.msgtouch.framework.cluster;

/**
 * Created by Dean on 2016/12/22.
 */
public class TouchApp {
    private long uid;
    private String gameId;


    private String getUserKey(){
        return gameId+"_"+uid;
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
}
