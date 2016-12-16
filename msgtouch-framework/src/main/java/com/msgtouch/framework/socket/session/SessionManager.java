package com.msgtouch.framework.socket.session;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Dean on 2016/11/29.
 */
public class SessionManager {
    private static SessionManager sessionManager=new SessionManager();

    private SessionManager(){}

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
        if(null!=se){
            if(isforce){
                se.disconnect(true);
            }else{
                return false;
            }
        }
        sessionMap.put(key,session);
        return true;
    }


    public boolean removeSession(ISession session){
        for(Map.Entry<String,ISession> entry:sessionMap.entrySet()){
            if(entry.getValue()==session){
                sessionMap.remove(entry.getKey());
            }
        }
        return false;
    }

    public ISession getSession(String key){
        return sessionMap.get(key);
    }


    public Collection<ISession> getAllSession(){
        return sessionMap.values();
    }


}
