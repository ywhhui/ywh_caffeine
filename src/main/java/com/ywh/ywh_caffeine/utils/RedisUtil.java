package com.ywh.ywh_caffeine.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.nio.charset.StandardCharsets;
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
     * set nx，上锁
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


    //redis加锁
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

    //redis解锁lua脚本 保证 锁删除原子性
    public boolean unlock(String key, String value) {
        try {
            //使用lua脚本保证删除的原子性，确保解锁
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] " +
                    "then return redis.call('del', KEYS[1]) " +
                    "else return 0 end";
            Boolean unlockState = (Boolean) redisTemplate.execute((RedisCallback<Boolean>) connection ->
                    connection.eval(script.getBytes(), ReturnType.BOOLEAN, 1,
                            key.getBytes(StandardCharsets.UTF_8), value.getBytes(StandardCharsets.UTF_8)));
            return unlockState == null || !unlockState;
        } catch (Exception e) {
            System.out.println("unLock failed for redis key: {}, value: {}"+key+ value);
            return false;
        }
    }


}
