package com.guojr;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.query.SortQuery;
import org.springframework.data.redis.core.query.SortQueryBuilder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class TestMain_03 {

    @Autowired
    private RedisTemplate redisTemplate;

    private ListOperations getOps(){
        return redisTemplate.opsForList();
    }

    private HashOperations opsHash(){
        return redisTemplate.opsForHash();
    }
    @Test
    public void testSort(){
        while (true){
            String value = (String) getOps().rightPop("sort-input");
            if(StringUtils.isEmpty(value)) break;
        }
        getOps().rightPushAll("sort-input","23","15","110","7");
        SortQuery sortQuery = SortQueryBuilder.sort("sort-input").alphabetical(true).build();//根据字母表排序
        List<String> list = redisTemplate.sort(sortQuery);
        for (String str:list)
            System.out.println(str);

        System.out.println("hash排序");
        opsHash().delete("h-7","field");
        opsHash().delete("h-15","field");
        opsHash().delete("h-23","field");
        opsHash().delete("h-110","field");

        opsHash().put("h-7","field","5");
        opsHash().put("h-15","field","1");
        opsHash().put("h-23","field","9");
        opsHash().put("h-110","field","3");
//        SortQuery sortHash = SortQueryBuilder.sort("sort-input").by("h-*->field").build();
        SortQuery sortHash2 = SortQueryBuilder.sort("sort-input").by("h-*->field")
                .get("h-*->field").build();
        List<String> listByHash = redisTemplate.sort(sortHash2);
        for (String str:listByHash)
            System.out.println(str);

    }
}
