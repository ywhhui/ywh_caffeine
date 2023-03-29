package com.ywh.ywh_caffeine.service.impl;

import com.google.common.base.Stopwatch;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ywh.ywh_caffeine.mapper.CaffeineTestMapper;
import com.ywh.ywh_caffeine.model.ToDoMsg;
import com.ywh.ywh_caffeine.service.RedisTestService;
import com.ywh.ywh_caffeine.utils.RedisUtil;
import com.ywh.ywh_caffeine.vo.ResultVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 *
 */
@Service
public class RedisTestServiceImpl implements RedisTestService {

    private static final Logger logger = LoggerFactory.getLogger(RedisTestServiceImpl.class);

    public static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    //分布式锁value
    private static String uuid = UUID.randomUUID().toString();

    @Autowired
    private CaffeineTestMapper caffeineTestMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Resource(name = "testTaskExecutor")
    private Executor executor;

    /**
     * 本地windows测试
     * @param todoMsg
     * @return
     * @throws Exception
     */
    @Override
    public ResultVo getList(ToDoMsg todoMsg) throws Exception {
        String jsonStr = gson.toJson(todoMsg);
        logger.info("getList todoMsg:{}",jsonStr);
        ResultVo resultVo = new ResultVo();
        String cacheKey = "redis_test_ywh2"+todoMsg.getId();
        Object cacheV = redisUtil.get(cacheKey);
//        Object cacheV = redisUtil.getJedisValue(cacheKey);
        if(null != cacheV){
//            Object cacheValue = redisUtil.get(cacheKey);
            Object cacheValue = redisUtil.getJedisValue(cacheKey);
            logger.info("cacheIfPresent:{}",gson.toJson(cacheValue));
            //jsonStr转json问题 需要注意
            if(null !=cacheValue ){
                resultVo.setData(cacheValue);
                return resultVo;
            }
        }
        List<ToDoMsg> query = caffeineTestMapper.query(new ToDoMsg());
        //String类型
        String strTest = "strTest0327001yyyy";
//        redisUtil.set(cacheKey,strTest);
        //json类型
        redisUtil.setex(cacheKey,query,1, TimeUnit.MINUTES);
        //jedispool连接方式
//        redisUtil.setJedisPoolValue(cacheKey,query);
        resultVo.setData(query);
        return resultVo;
    }

    /**
     * 分布式锁测试
     * @param todoMsg
     * @return
     * @throws Exception
     */
    @Override
    public ResultVo luaTest(ToDoMsg todoMsg) throws Exception {
        Stopwatch stopwatch = Stopwatch.createStarted();
        ResultVo resultVo = new ResultVo();
        //redisTemplate lua脚本方式
        //配置分布式锁锁，设置随机uuid进行验证防止误删
//        uuid = UUID.randomUUID().toString();
        //初始化业务库存
//        redisUtil.set("productNum", 1000 + "");

        //异步执行
//        CompletableFuture.runAsync(()->{
//            excuteTask(productTotal);
//        },executor).exceptionally(e->{
//            System.out.println("异步执行失败"+e);
//            return null;
//        });
        //同步执行

        Object productNum = redisUtil.get("productNum");
        int productTotal = Integer.parseInt(productNum + "");
        //业务逻辑
        boolean lock = redisUtil.setNx("lock",uuid,1);
        if(lock){
            //若分布式锁加锁成功 表示拿到锁 那么就执行业务 从缓存中获取业务的库存锁
            synchronized (this){
                //2.2有值就转成成int 表示库存
                if(productTotal == 0){
                    System.out.println("商品不存在 库存不足");
                    logger.info("未拿到锁的用户 没货了 商品不存在 库存不足");
                    //2.4释放分布式锁，del，保证锁必须被释放-->当业务执行时间小与过期时间时需要释放锁
                    redisUtil.unlock2("lock",uuid);
                    resultVo.setMessage("未拿到锁的用户 没货了");
                    return resultVo;
                }
                //2.3把redis的num加1
                if(productTotal >0){
                    int shenYude = productTotal - 1;
                    //更新库存
                    redisUtil.set("productNum", shenYude + "");
                    System.out.println("库存当前为：" + shenYude);
                    logger.info("拿到锁的用户 并买到了东西 库存当前为 realStock:{}",shenYude);
                    //2.4释放分布式锁，del，保证锁必须被释放-->当业务执行时间小与过期时间时需要释放锁
                    redisUtil.unlock2("lock",uuid);
                    resultVo.setMessage("拿到锁的用户 并买到了东西");
                    return resultVo;
                }
            }
        }else {
            //上锁失败 没有获取到锁 锁还没有过期 或者还没有被另一个线程释放
            try {
                //休息一会儿 再去尝试上锁
                Thread.sleep(100);
                System.out.println("再次去获取锁 自旋！");
                logger.info("未拿到锁的用户 循环去买 分布式锁未释放 再次去获取锁 自旋！");
                resultVo.setMessage("未拿到锁的用户 循环去买");
                luaTest(todoMsg);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        stopwatch.stop();

        resultVo.setData("结束"+uuid);
        System.out.println("耗时对比--"+stopwatch.elapsed(TimeUnit.MILLISECONDS));
        return resultVo;
    }

    //lua脚本 集合RedisTemplate 实现并发
    private ResultVo excuteTask(int productTotal,ResultVo resultVo) {

        return resultVo;
    }

    //redisson方式
//        String lockKey = UUID.randomUUID().toString();
//        RLock lock = redisson.getLock(lockKey);       //获取锁
//        try {
//            lock.lock();    //上锁
//            log.info("锁已开启");
//            synchronized (this){
//                if(redisUtil.get("product")==null){
//                    log.error("商品不存在！");
//                }else{
//                    //获取当前库存
//                    int stock = Integer.parseInt(redisUtil.get("product").toString());
//                    if (stock > 0){
//                        int realStock = stock - 1;
//                        //更新库存
//                        redisUtil.set("product", realStock + "");
//                        log.info("库存当前为：" + realStock);
//                    }else {
//                        log.warn("扣减失败，库存不足！");
//                    }
//                }
//            }
//        }catch (Exception e){
//            log.warn("系统错误，稍后重试");
//        }
//        finally {
//            lock.unlock();    //删除锁
//            log.info("锁已关闭");
//        }


}
