package com.ywh.ywh_caffeine.utils;

import com.google.common.base.Stopwatch;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ywh.ywh_caffeine.model.ToDoMsg;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 模拟并发
 */
@RestController
public class TestMultiUserReq {

    private static final Logger logger = LoggerFactory.getLogger(TestMultiUserReq.class);

    public static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

    public static void main(String[] args) throws Exception {
        sendMultiReq();
    }

    private static void sendMultiReq(){
        //线程池设置2000个核心线程数，最大5000
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2000, 5000, 2, TimeUnit.MINUTES,
                new ArrayBlockingQueue<Runnable>(5000));
        //设置计数器，计数器的作用：类似于赛道上有多个运动员同时准备起跑时，那个发令枪的作用，枪一响起跑线上的运动员同时开跑
        CountDownLatch latch = new CountDownLatch(1);
        //模拟100个用户
        int userCount = 100;//同一时间请求的用户数量
        for (int i = 0; i < userCount; i++) {
            ClientUser analogUser = new ClientUser(latch);
            executor.execute(analogUser);
        }
        //计数器減一  所有线程释放 并发访问。
        latch.countDown();
        executor.shutdown();
    }
     static class ClientUser implements Runnable {
        CountDownLatch latch;
        public ClientUser(CountDownLatch latch) {
            this.latch = latch;

        }

        @Override
        public void run() {
            try {
                latch.await();
                //这里写你请求的接口逻辑代码
                try {
                    sendPostJsonMsg("http://127.0.0.1:9003/redis/test/luaTest",gson.toJson(new ToDoMsg()));
                    logger.info("sendPostJsonMsg end");
                } catch (Exception e) {
                   logger.info("sendPostJsonMsg error!",e);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    //参数json转换
    private static MediaType mediaType = MediaType.parse("application/json;charset=utf-8");

    //初始化 6秒超时
    private  static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60,TimeUnit.SECONDS)
            .readTimeout(60,TimeUnit.SECONDS)
            .build();

    /**
     * 同步post
     * @param url
     * @param param
     * @return
     */
    public static int sendPostJsonMsg(String url,String param){
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            //初始化参数 如果有的话
            RequestBody body = RequestBody.create(mediaType,param);
            Request.Builder builder = new Request.Builder().url(url).post(body);
            //初始化请求头
            builder.addHeader("Content-Type","application/json");
            //获取请求信息
            Request request = builder.build();
            //同步post方式
            Response response = okHttpClient.newCall(request).execute();
            stopwatch.stop();
            String result = response.body().string();
//            System.out.println("同步post返回result"+result+"-耗时"+stopwatch.elapsed(TimeUnit.MILLISECONDS));
        } catch (Exception e) {
            stopwatch.stop();
            System.out.println("报错e"+e+stopwatch.elapsed(TimeUnit.MILLISECONDS));
            return -1;
        }
        return 1;
    }



}
