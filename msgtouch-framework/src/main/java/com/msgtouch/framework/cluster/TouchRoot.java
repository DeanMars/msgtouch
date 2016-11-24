package com.msgtouch.framework.cluster;


import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dean on 2016/11/22.
 */
public class TouchRoot {

    List<TouchService> services=new ArrayList<TouchService>();

    public List<TouchService> getServices() {
        return services;
    }

    public void setServices(List<TouchService> services) {
        this.services = services;
    }

    public void addServices(TouchService touchService){
        if(null!=touchService){
            services.add(touchService);
        }
    }

    public boolean contailService(String serviceName){
        if(StringUtils.isNoneEmpty(serviceName)) {
            for (TouchService touchService : services) {
                if (serviceName.equals(touchService.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public TouchService getSerrviceByName(String serviceName){
        TouchService result=null;
        if(StringUtils.isNoneEmpty(serviceName)) {
            for (TouchService touchService : services) {
                if (serviceName.equals(touchService.getName())) {
                    result=touchService;
                }
            }
        }
        return result;
    }

}
