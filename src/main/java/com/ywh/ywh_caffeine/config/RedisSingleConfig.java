package com.ywh.ywh_caffeine.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.Serializable;

/**
 * 单机版redis  实例化过程 lettuce 和 jedis 两个客户端对比
 * 1.Lettuce 是 一种可伸缩，线程安全，完全非阻塞的Redis客户端，多个线程可以共享一个RedisConnection,它利用Netty NIO 框架来高效地管理多个连接，从而提供了异步和同步数据访问方式，用于构建非阻塞的反应性应用程序
 * 2.使用jedis的连接池
 */
@Configuration
public class RedisSingleConfig {

    /**
     *   Lettuce客户端初始化配置 设置序列化器 支持json对象 value
     * @param redisConnectionFactory
     * @return
     */
   /* @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
//        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//        // 方法过期，改为下面代码
//        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance ,
//                ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
//        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
//        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        RedisTemplate<String, Object> redisTemplate = null;
        try {
            redisTemplate = new RedisTemplate<>();
            redisTemplate.setConnectionFactory(redisConnectionFactory);
            // 设置key序列化方式
            redisTemplate.setKeySerializer(new StringRedisSerializer());
            // 设置value序列化方式 stirng方式和json方式
            redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
//            redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
            // 设置hash key序列化方式
            redisTemplate.setHashKeySerializer(new StringRedisSerializer());
            // 设置hash value序列化方式
            redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
//            redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        } catch (Exception e) {
            System.out.println("redis 配置error"+e+redisTemplate);
        }
        System.out.println("redis 配置 初始化"+redisTemplate);
        return redisTemplate;
    }*/

    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private int port;
    @Value("${spring.redis.password}")
    private String password;

    private static JedisConnectionFactory factory;

    /**
     * jedis 客户端连接redis服务 先初始化JedisConnectionFactory
     * @return
     */
    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        if(null == factory){
            RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
            config.setHostName(host);
            config.setPort(port);
            config.setPassword(password);
            factory = new JedisConnectionFactory(config);
        }
        return factory;
    }
    //factory 初始化后
    @Bean
    public RedisTemplate<String, Serializable> redisTemplate() {
        RedisTemplate<String, Serializable> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        return redisTemplate;
    }

    //jedis pool 连接方式
    @Autowired
    private RedisJedisConfig redisJedisConfig;

    @Bean
    public JedisPool JedisPoolFactory() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(redisJedisConfig.getPoolMaxIdle());
        poolConfig.setMaxTotal(redisJedisConfig.getPoolMaxTotal());
        poolConfig.setMaxWaitMillis(redisJedisConfig.getPoolMaxWait()* 1000);
        JedisPool jp = new JedisPool(poolConfig, redisJedisConfig.getHost(), redisJedisConfig.getPort(),
                redisJedisConfig.getTimeout()*1000, redisJedisConfig.getPassword(), 0);
        return jp;
    }

}
