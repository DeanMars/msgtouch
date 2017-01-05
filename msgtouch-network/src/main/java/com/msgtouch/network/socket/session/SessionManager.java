package com.msgtouch.network.socket.session;

import com.msgtouch.network.registry.RegistryEngine;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Dean on 2016/11/29.
 */
public class SessionManager {
    private static SessionManager sessionManager=new SessionManager();
    private static RegistryEngine registryEngine;
    private SessionManager(){}

    public void initRegistryEngine(RegistryEngine registryEngine){
        this.registryEngine=registryEngine;
    }

    public static SessionManager getInstance(){
        return sessionManager;
    }

    private static Map<String,ISession> sessionMap=new  ConcurrentHashMap<String, ISession>();

    public void addAnonymousSession(ISession session){
        if(!containSession(session)){
            String uuid= UUID.randomUUID().toString();
            sessionMap.put(uuid,session);

        }
    }

    public boolean containSession(ISession session){
        for(ISession item:sessionMap.values()){
            if(item==session){
                return true;
            }
        }
        return false;
    }


    public boolean regesterSession(String key,ISession session,boolean isforce) {
        ISession se=sessionMap.get(key);
        if(null!=se&&se!=session){
            if(isforce){
                se.disconnect(true);
            }else{
                return false;
            }
        }
        session.setAttribute(Session.USER_KEY,key);
        sessionMap.put(key,session);
        return true;
    }


    public void removeSession(ISession session){
        for(Map.Entry<String,ISession> entry:sessionMap.entrySet()){
            if(entry.getValue()==session){
                sessionMap.remove(entry.getKey());
            }
        }
        String userKey=session.getAttribute(Session.USER_KEY);
        if(null!=userKey){
            String []args=userKey.split("_");
            String gameId=args[0];
            long uid=Long.parseLong(args[1]);
            registryEngine.loginOutApp(uid,gameId);
        }

    }

    public ISession getSession(String key){
        return sessionMap.get(key);
    }


    public Collection<ISession> getAllSession(){
        return sessionMap.values();
    }


    public String getUserKey(long uid,String gameId){
        return gameId+"_"+uid;
    }


}
