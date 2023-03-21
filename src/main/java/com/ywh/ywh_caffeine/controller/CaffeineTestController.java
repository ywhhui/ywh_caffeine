package com.ywh.ywh_caffeine.controller;

import com.ywh.ywh_caffeine.service.CaffeineTestService;
import com.ywh.ywh_caffeine.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("caffeine")
public class CaffeineTestController {

    @Autowired
    private CaffeineTestService caffeineTestService;

    @GetMapping("/query")
    public ResultVo query() throws Exception {
        return caffeineTestService.query();
    }
}
