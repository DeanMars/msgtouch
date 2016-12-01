package com.msgtouch.toucher.service;

import com.msgtouch.common.vo.TestVo;
import com.msgtouch.framework.socket.session.ISession;
import com.msgtouch.framework.socket.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Dean on 2016/11/29.
 */
@Service
public class PushServiceImpl {
    private  static Logger logger= LoggerFactory.getLogger(PushServiceImpl.class);

    public void pushAll(String msg){
        Collection<ISession> list=SessionManager.getInstance().getAllSession();

        TestVo testVo=new TestVo();
        testVo.setRequest(msg);

        for(ISession session:list){
            try {
                TestVo ret= session.pushMsg( testVo, 10);
                logger.info("pushAll ret testVo Request={},Response={}",ret.getRequest(),ret.getResponse());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }

    }

}
