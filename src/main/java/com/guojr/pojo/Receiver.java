package com.guojr.pojo;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CountDownLatch;

public class Receiver {

    @Autowired
    private CountDownLatch latch;

    public void onMessage(String message){
        System.out.println("接受到消息:"+message);
        latch.countDown();
    }
}
