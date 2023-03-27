package com.ywh.ywh_caffeine.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 带过期的
     * @param key
     * @param value
     * @param time
     * @param timeUnit
     */
    public void setex(String key,Object value,long time,TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, time, timeUnit);
    }

    /**
     * set
     * String类型的set,无过期时间
     * @param key key
     * @param value value
     */
    public void set(String key, Object value){
        redisTemplate.opsForValue().set(key,value);
    }

    /**
     * 如果key不存在，则设置
     * @param key  key
     * @param value value
     * @return 返回是否成功
     */
    public Boolean setnx(String key,Object value){
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    /**
     * 根据key获取值
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    @Autowired
    private JedisPool jedisPool;

    public <T> T getJedisValue(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String str = jedis.get(key);
            T t = ( T)str;
            return t;
        } finally {
            returnToPool(jedis);
        }
    }

    public <T> boolean setJedisPoolValue(String key, Object value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            if (value == null) {
                return false;
            }
            jedis.set(key, value.toString());
            return true;
        } finally {
            returnToPool(jedis);
        }
    }

    private void returnToPool(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

}
