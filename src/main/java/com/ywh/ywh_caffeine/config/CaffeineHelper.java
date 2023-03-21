package com.ywh.ywh_caffeine.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class CaffeineHelper {

    private static final Logger logger = LoggerFactory.getLogger(CaffeineHelper.class);
    /**
     * 缓存有效期
     */
    private static long cacheExpire;

    /**
     * 默认最大容量
     */
    private static long maximunSize;

    private volatile static Cache<String,Object> cache ;

    @Value("${caffeine.cacheExpire:2}")
    public void setCacheExpire(long cacheExpire) {
        CaffeineHelper.cacheExpire = cacheExpire;
    }

    @Value("${caffeine.maximunSize:500000}")
    public void setMaximunSize(long maximunSize) {
        CaffeineHelper.maximunSize = maximunSize;
    }

    /**
     *  同步锁 随服务启动 只实例化一次
     * @return
     */
    public static Cache<String, Object> getCache() {

        if(null == cache){
            logger.info("getCache开始初始化");
            synchronized (CaffeineHelper.class){
                if(null == cache){
                    cache = Caffeine.newBuilder().maximumSize(maximunSize).expireAfterAccess(cacheExpire, TimeUnit.MINUTES).build();
                }
            }
        }
        logger.info("getCache初始化完成");
        return cache;
    }

    /**
     * 当前所有生效的key 失效
     */
    public static void invalidateAll(){
        logger.info("test_ywh value:{}",cache.getIfPresent("test_ywh"));
        cache.invalidateAll();
        logger.info("test_ywh end value:{}",cache.getIfPresent("test_ywh"));
    }
}
