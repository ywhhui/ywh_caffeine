package com.ywh.ywh_caffeine.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ywh.ywh_caffeine.config.CaffeineConfig;
import com.ywh.ywh_caffeine.config.CaffeineHelper;
import com.ywh.ywh_caffeine.mapper.CaffeineTestMapper;
import com.ywh.ywh_caffeine.model.ToDoMsg;
import com.ywh.ywh_caffeine.service.CaffeineTestService;
import com.ywh.ywh_caffeine.vo.ResultVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Service
@DependsOn(value = {"caffeineHelper"})
public class CaffeineTestServiceImpl implements CaffeineTestService {

    private static final Logger logger = LoggerFactory.getLogger(CaffeineTestServiceImpl.class);

    public static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    private static final Cache<String,Object> cache = CaffeineHelper.getCache();

    @Autowired
    private CaffeineTestMapper caffeineTestMapper;

    @Autowired
    private CaffeineConfig caffeineConfig;

    @Override
    public ResultVo query(String accout) throws Exception {
        logger.info("query accout:{}",accout);
        ResultVo resultVo = new ResultVo();
        String cacheKey = "test_ywh";
        if(caffeineConfig.isEnableCache()){
            Object cacheIfPresent = cache.getIfPresent(cacheKey);
            logger.info("cacheIfPresent:{}",gson.toJson(cacheIfPresent));
            if(null !=cacheIfPresent ){
                resultVo.setData(cacheIfPresent);
                return resultVo;
            }
        }
        List<ToDoMsg> query = caffeineTestMapper.query(new ToDoMsg());
        if(!CollectionUtil.isEmpty(query)){
            cache.put(cacheKey,query);
        }
        resultVo.setData(query);
        return resultVo;
    }

    @Override
    public ResultVo getList(ToDoMsg todoMsg) throws Exception {
        String jsonStr = gson.toJson(todoMsg);
        logger.info("getList todoMsg:{}",jsonStr);
        ResultVo resultVo = new ResultVo();
        String cacheKey = "test_ywh2";
        if(caffeineConfig.isEnableCache()){
            Object cacheIfPresent = cache.getIfPresent(cacheKey);
            logger.info("cacheIfPresent:{}",gson.toJson(cacheIfPresent));
            if(null !=cacheIfPresent ){
                resultVo.setData(cacheIfPresent);
                return resultVo;
            }
        }
        List<ToDoMsg> query = caffeineTestMapper.query(new ToDoMsg());
        if(!CollectionUtil.isEmpty(query)){
            cache.put(cacheKey,query);
        }
        resultVo.setData(query);
        return resultVo;
    }
}
