#单机
##1.两种客户端连接方式 jedis 和lettuce. 在 springboot 1.5.x版本的默认的Redis客户端是 Jedis实现的，springboot 2.x版本中默认客户端是用 lettuce实现的。
jedis连接方式如下： 需要手动配置
<dependency>
         <groupId>org.springframework.boot</groupId>
         <artifactId>spring-boot-starter-data-redis</artifactId>
          <exclusions>
                  <exclusion>
                    <groupId>io.lettuce</groupId>
                    <artifactId>lettuce-core</artifactId>
                  </exclusion>
          </exclusions>
</dependency>
<dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
</dependency>

spring.redis.host=localhost
spring.redis.port=6379
spring.redis.password=root
spring.redis.jedis.pool.max-idle=8
spring.redis.jedis.pool.max-wait=-1ms
spring.redis.jedis.pool.min-idle=0
spring.redis.jedis.pool.max-active=8

##2.lettuce jedis 区别
    lettuce：底层是用netty实现，线程安全，默认只有一个实例。
    jedis：可直连redis服务端，配合连接池使用，可增加物理连接。
    
#3.lettuce客户端虽然支持api同步异步通信，但是在请求多次请求超时后 不再自动重连。 比如当redis 切换集群节点 发生重连超时时候 redis会挂掉。 不推荐使用Lettuce客户端