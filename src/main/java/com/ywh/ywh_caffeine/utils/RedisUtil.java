package com.ywh.ywh_caffeine.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    private static final Logger logger = LoggerFactory.getLogger(RedisUtil.class);

//    @Autowired
//    private RedisTemplate redisTemplate;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

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
     * set nx，上锁 加锁，如果加锁成功则返回true，否则返回false
     * @param key 一般设为lock
     *@param value 一般使用uuid
     *@param time 缓存时间，单位为s
     */
    public boolean setNx(String key, String value, int time){
        return redisTemplate.opsForValue().setIfAbsent(key, value, time, TimeUnit.SECONDS);
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


    //redis加锁 另外一种写法
    public boolean lock(String key, String value, long timeout, TimeUnit timeUnit) {
        Boolean locked;
        try {
            //SET_IF_ABSENT --> NX: Only set the key if it does not already exist.
            //SET_IF_PRESENT --> XX: Only set the key if it already exist.
            locked = (Boolean) redisTemplate.execute((RedisCallback<Boolean>) connection ->
                    connection.set(key.getBytes(StandardCharsets.UTF_8), value.getBytes(StandardCharsets.UTF_8),
                            Expiration.from(timeout, timeUnit), RedisStringCommands.SetOption.SET_IF_ABSENT));
        } catch (Exception e) {
            System.out.println("Lock failed for redis key: {}, value: {}"+ key+value);
            locked = false;
        }
        return locked != null && locked;
    }

    /**
     * 需要释放的分布式锁 lua脚本保证get del的原子性
     * @param key
     * @param value
     * @return
     */
    public boolean unlock(String key, String value){
        try {
            String script = "if redis.call('get',KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
            DefaultRedisScript<Boolean> redisScript = new DefaultRedisScript<>();
            redisScript.setResultType(Boolean.class);
//            redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("unlock.lua"))); script配置路径
            redisScript.setScriptText(script);
            Boolean executeResult = redisTemplate.execute(redisScript, Arrays.asList(key), value);
            //1表示del锁成功 0表示del锁失败  true表示del锁成功 false表示del锁失败
            System.out.println(executeResult);
            logger.info("unlock 释放锁的状态 executeResult:{}",executeResult);
            return executeResult;
        } catch (Exception e) {
            System.out.println("unLock failed for redis key: {}, value: {}"+key+ value);
            return false;
        }

    }


}
