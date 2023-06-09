package com.ywh.ywh_caffeine.service;

import com.ywh.ywh_caffeine.model.ToDoMsg;
import com.ywh.ywh_caffeine.vo.ResultVo;

/**
 * caffeine相关验证
 */
public interface CaffeineTestService {


    ResultVo query(String accout) throws Exception;

    ResultVo getList(ToDoMsg todoMsg) throws Exception;
}
