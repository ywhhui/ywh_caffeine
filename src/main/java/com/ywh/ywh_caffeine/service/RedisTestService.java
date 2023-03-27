package com.ywh.ywh_caffeine.service;

import com.ywh.ywh_caffeine.model.ToDoMsg;
import com.ywh.ywh_caffeine.vo.ResultVo;

/**
 * redis 测试
 */
public interface RedisTestService {

    ResultVo getList(ToDoMsg todoMsg) throws Exception;
}
