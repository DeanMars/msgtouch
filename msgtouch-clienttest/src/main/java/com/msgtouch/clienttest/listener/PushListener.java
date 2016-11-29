package com.msgtouch.clienttest.listener;

import com.msgtouch.common.vo.TestVo;
import com.msgtouch.framework.socket.dispatcher.MsgPushedListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Dean on 2016/11/29.
 */
public class PushListener implements MsgPushedListener<TestVo> {
    private  static Logger logger= LoggerFactory.getLogger(PushListener.class);
    @Override
    public TestVo msgReceived(TestVo testVo) {
        logger.info("msgReceived testVo Request={},Response={}",testVo.getRequest(),testVo.getResponse());

        testVo.setResponse("Response123421312");

        return testVo;

    }








}
