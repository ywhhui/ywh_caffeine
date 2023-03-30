package com.ywh.ywh_caffeine.utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * AtomicInteger 计数用 使用并发场景 cas保证原子性 volatile 只保证可见性 无法保证原子性
 * 对于jdk1.8的并发包来说，底层基本上就是通过Usafe和CAS机制来实现的。有好处也肯定有一个坏处。从好的方面来讲，就是上面AtomicInteger类可以保持其原子性。但是从坏的方面来看，Usafe因为直接操作的底层地址，肯定不是那么安全，而且CAS机制也伴随着大量的问题，比如说有名的ABA问题等等。关于CAS机制，我也会在后续的文章中专门讲解。大家可以先根据那个给儿子订婚的例子有一个基本的认识。
 */
public class AtomicIntegerTest {
    static AtomicInteger a2 = new AtomicInteger(0);
    private static volatile int a = 0;
    public static void main(String[] args) {
        Thread[] threads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            threads[i] = new Thread(()->{
                try {
                    for (int j = 0; j < 10; j++) {
                        //自增
                        System.out.println(a++);
                        System.out.println(a2.incrementAndGet());
                        Thread.sleep(500);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            threads[i].start();
        }
    }

}
