package com.ywh.ywh_caffeine.controller;

import com.ywh.ywh_caffeine.model.ToDoMsg;
import com.ywh.ywh_caffeine.service.RedisTestService;
import com.ywh.ywh_caffeine.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * redis 单个redis 非集群 验证缓存数据类型string json
 */
@RestController
@RequestMapping("redis/test")
public class RedisTestController {

    @Autowired
    private RedisTestService redisTestService;

    @PostMapping("/getList")
    public ResultVo getList(@RequestBody ToDoMsg todoMsg) throws Exception {
        return redisTestService.getList(todoMsg);
    }
}
