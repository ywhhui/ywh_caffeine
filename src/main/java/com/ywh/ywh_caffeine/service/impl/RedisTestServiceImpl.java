package com.ywh.ywh_caffeine.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ywh.ywh_caffeine.mapper.CaffeineTestMapper;
import com.ywh.ywh_caffeine.model.ToDoMsg;
import com.ywh.ywh_caffeine.service.RedisTestService;
import com.ywh.ywh_caffeine.utils.RedisUtil;
import com.ywh.ywh_caffeine.vo.ResultVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 */
@Service
public class RedisTestServiceImpl implements RedisTestService {

    private static final Logger logger = LoggerFactory.getLogger(RedisTestServiceImpl.class);

    public static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    @Autowired
    private CaffeineTestMapper caffeineTestMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public ResultVo getList(ToDoMsg todoMsg) throws Exception {
        String jsonStr = gson.toJson(todoMsg);
        logger.info("getList todoMsg:{}",jsonStr);
        ResultVo resultVo = new ResultVo();
        String cacheKey = "redis_test_ywh2"+todoMsg.getId();
        Object cacheV = redisUtil.get(cacheKey);
//        Object cacheV = redisUtil.getJedisValue(cacheKey);
        if(null != cacheV){
//            Object cacheValue = redisUtil.get(cacheKey);
            Object cacheValue = redisUtil.getJedisValue(cacheKey);
            logger.info("cacheIfPresent:{}",gson.toJson(cacheValue));
            //jsonStr转json问题 需要注意
            if(null !=cacheValue ){
                resultVo.setData(cacheValue);
                return resultVo;
            }
        }
        List<ToDoMsg> query = caffeineTestMapper.query(new ToDoMsg());
        //String类型
        String strTest = "strTest0327001yyyy";
//        redisUtil.set(cacheKey,strTest);
        //json类型
        redisUtil.setex(cacheKey,query,1, TimeUnit.MINUTES);
        //jedispool连接方式
//        redisUtil.setJedisPoolValue(cacheKey,query);
        resultVo.setData(query);
        return resultVo;
    }
}
