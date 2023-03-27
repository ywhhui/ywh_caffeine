package com.ywh.ywh_caffeine.controller;

import com.ywh.ywh_caffeine.model.ToDoMsg;
import com.ywh.ywh_caffeine.service.CaffeineTestService;
import com.ywh.ywh_caffeine.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * caffeine 本地缓存 验证单例模式 验证缓存失效方法
 */
@RestController
@RequestMapping("caffeine")
public class CaffeineTestController {

    @Autowired
    private CaffeineTestService caffeineTestService;

    @GetMapping("/query")
    public ResultVo query(@RequestParam String accout) throws Exception {
        return caffeineTestService.query(accout);
    }

    @PostMapping("/getList")
    public ResultVo getList(@RequestBody ToDoMsg todoMsg) throws Exception {
        return caffeineTestService.getList(todoMsg);
    }
}
