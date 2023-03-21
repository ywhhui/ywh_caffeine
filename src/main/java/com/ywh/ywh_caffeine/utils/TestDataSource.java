package com.ywh.ywh_caffeine.utils;

import org.jasypt.util.text.BasicTextEncryptor;

public class TestDataSource {
    public static void main(String[] args) {
        // 加密
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        //自己设置的秘钥
        textEncryptor.setPassword("test");

        String userName = textEncryptor.encrypt("root");
        System.out.println(userName);

        // 解密
        BasicTextEncryptor textEncryptor2 = new BasicTextEncryptor();
        textEncryptor2.setPassword("test");
        String oldPassword = textEncryptor2.decrypt(userName);
        System.out.println(oldPassword);
        System.out.println("--------------------------");

    }
}
