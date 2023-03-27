package com.ywh.ywh_caffeine.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * jedis pool 连接方式
 */
@Component
@Data
public class RedisJedisConfig {

    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private int port;
    @Value("${spring.redis.password}")
    private String password;

    @Value("${spring.redis.jedis.pool.max-active}")
    private int poolMaxTotal;
    @Value("${spring.redis.jedis.pool.max-idle}")
    private int poolMaxIdle;
    @Value("${spring.redis.jedis.pool.max-wait}")
    private int poolMaxWait;
    @Value("${spring.redis.timeout:3}")
    private int timeout;



}
