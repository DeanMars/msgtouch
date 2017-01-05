package com.msgtouch.network.registry;

/**
 * Created by Dean on 2017/1/4.
 */
public interface RegistryEngine {

    void loginApp(long uid,String gameId);

    void loginOutApp(long uid,String gameId);

}
