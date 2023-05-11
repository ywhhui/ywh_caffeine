package com.ywh.ywh_caffeine.service;

import com.ywh.ywh_caffeine.model.ToDoMsg;
import com.ywh.ywh_caffeine.vo.ResultVo;

/**
 * redis 测试
 */
public interface RedisTestService {

    /**
     * RedisTemplate实例化测试
     * @param todoMsg
     * @return
     * @throws Exception
     */
    ResultVo getList(ToDoMsg todoMsg) throws Exception;

    /**
     * 分布式锁测试lua脚本写法
     * @param todoMsg
     * @return
     * @throws Exception
     */
    ResultVo luaTest(ToDoMsg todoMsg)throws Exception;

    /**
     *分布式锁测试 redisson写法
     * @param todoMsg
     * @return
     */
    ResultVo luaTest2(ToDoMsg todoMsg)throws Exception;

    ResultVo rarMm(ToDoMsg todoMsg)throws Exception;
}
