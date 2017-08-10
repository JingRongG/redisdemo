package com.guojr;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.jws.soap.SOAPBinding;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class TestMain_04 {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testRedisTrans(){
        redisTemplate.delete("notrans:");
        try {
            Thread thread1 = new Thread(new TestTrans());
            Thread thread2 = new Thread(new TestTrans());
            Thread thread3 = new Thread(new TestTrans());
            thread1.start();
            thread2.start();
            thread3.start();
            new Runnable(){
                @Override
                public void run() {
                    while (true){
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println(redisTemplate.opsForValue().get("notrans:"));
                        if(!thread3.isAlive()) break;
                    }
                }
            }.run();
        }catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }


    }

    private class TestTrans implements Runnable{
        @Override
        public void run() {
            final byte[] key = redisTemplate.getKeySerializer().serialize("notrans:");
            int i = 100000;
            while (true){
                // 不使用事务
//                redisTemplate.opsForValue().increment("notrans:",1);
//                redisTemplate.opsForValue().increment("notrans:",-1);

                /**
                 * 使用事务 移除竞争条件
                 *
                 *  redisTemplate.multi();
                 *  redisTemplate.execute();
                 *  使用multi与execute会出现不在一个session的问题
                 */

                RedisCallback<List<Object>> pipeline = new RedisCallback<List<Object>>() {
                    @Override
                    public List<Object> doInRedis(RedisConnection connection) throws DataAccessException {
                        connection.openPipeline();
                        connection.incrBy(key,1);
                        connection.incrBy(key,-1);
                        return connection.closePipeline();
                    }
                };

                redisTemplate.execute(pipeline);
                if(i-- < 0)break;
            }
        }
    }

}
