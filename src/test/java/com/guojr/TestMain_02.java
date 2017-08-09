package com.guojr;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.CountDownLatch;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class TestMain_02 {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private CountDownLatch latch;

    @Test
    public void testPubSub() throws InterruptedException {
        new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while(true){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    redisTemplate.convertAndSend("channel",Integer.toString(i++));
                    if(i>99)break;
                }

            }
        }.run();

        latch.wait();
    }
}
