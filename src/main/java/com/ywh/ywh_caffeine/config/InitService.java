package com.ywh.ywh_caffeine.config;

import net.sf.sevenzipjbinding.SevenZip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

/**
 * 使用ApplicationRunner在程序启动完成后，自动执行一下initSevenZipFromPlatformJAR方法，先将sevenzipjbinding需要的依赖完成初始化。
 */
//@Component
public class InitService implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(InitService.class);
    @Override
    public void run(ApplicationArguments args) throws Exception {
        SevenZip.initSevenZipFromPlatformJAR();
        logger.info("初始化sevenzipjbinding相关依赖完成！");
    }
}
