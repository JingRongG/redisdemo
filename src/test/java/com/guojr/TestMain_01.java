package com.guojr;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class TestMain_01 {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 有序集合 zset
     */
    @Test
    public void zsetTest(){
        //相当于执行 zremrangebyrank zset-1 0 -1 命令
        redisTemplate.opsForZSet().removeRange("zset-1",0,-1);
        redisTemplate.opsForZSet().removeRange("zset-2",0,-1);

        ZSetOperations.TypedTuple<String> objectTypedTuple1 = new DefaultTypedTuple<String>("a",1d);
        ZSetOperations.TypedTuple<String> objectTypedTuple2 = new DefaultTypedTuple<String>("b",2d);
        ZSetOperations.TypedTuple<String> objectTypedTuple3 = new DefaultTypedTuple<String>("c",3d);
        Set<ZSetOperations.TypedTuple<String>> zset_1 = new HashSet<>();
        zset_1.add(objectTypedTuple1);
        zset_1.add(objectTypedTuple2);
        zset_1.add(objectTypedTuple3);
        Long res1 = redisTemplate.opsForZSet().add("zset-1",zset_1);
        System.out.println("有序集合zset-1添加完成："+res1);


        ZSetOperations.TypedTuple<String> objectTypedTuple11 = new DefaultTypedTuple<String>("b",4d);
        ZSetOperations.TypedTuple<String> objectTypedTuple22 = new DefaultTypedTuple<String>("c",1d);
        ZSetOperations.TypedTuple<String> objectTypedTuple33 = new DefaultTypedTuple<String>("d",0d);
        Set<ZSetOperations.TypedTuple<String>> zset_2 = new HashSet<>();
        zset_2.add(objectTypedTuple11);
        zset_2.add(objectTypedTuple22);
        zset_2.add(objectTypedTuple33);
        Long res2 = redisTemplate.opsForZSet().add("zset-2",zset_2);
        System.out.println("有序集合zset-2添加完成:"+res2);

        //交集运算
        redisTemplate.opsForZSet().intersectAndStore("zset-1","zset-2","zset-i");
        Set<ZSetOperations.TypedTuple<String>> tuples_i = redisTemplate.opsForZSet().rangeWithScores("zset-i",0,-1);
        Iterator<ZSetOperations.TypedTuple<String>> iterator_i = tuples_i.iterator();
        System.out.println("\n交集结果");
        while (iterator_i.hasNext()){
            ZSetOperations.TypedTuple<String> typedTuple = iterator_i.next();
            System.out.println("value:"+typedTuple.getValue()+" socre:" + typedTuple.getScore());
        }

        //并集运算
        redisTemplate.opsForZSet().unionAndStore("zset-1","zset-2","zset-u");
        Set<ZSetOperations.TypedTuple<String>> tuples_u = redisTemplate.opsForZSet().rangeWithScores("zset-u",0,-1);
        Iterator<ZSetOperations.TypedTuple<String>> iterator_u = tuples_u.iterator();
        System.out.println("\n并集结果:");
        while (iterator_u.hasNext()){
            ZSetOperations.TypedTuple<String> typedTuple = iterator_u.next();
            System.out.println("value:"+typedTuple.getValue()+" socre:" + typedTuple.getScore());
        }

    }
}
