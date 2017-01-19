package com.msgtouch.network.socket.client;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;

/**
 * Created by Dean on 2017/1/19.
 */
public class DefaultToucherHelper implements ITouchHostHelper {
    public Toucher getToucher() {

        String ipAddress="192.168.21.40";
        int port=8001;
       /* try {
            String response=HttpUtil.readContentFromGet("http://192.168.21.40:8084/getHost?gameId=050200&uid=123","utf-8");
            JSONObject json=JSONObject.parseObject(response);
            JSONObject data=json.getJSONObject("data");
            ipAddress=data.getString("ip");
            port=data.getInteger("port");
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        Toucher result=new Toucher();
        result.setIpAddress(ipAddress);
        result.setPort(port);
        return result;
    }
}
