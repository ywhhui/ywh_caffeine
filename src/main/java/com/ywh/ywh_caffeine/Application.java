package com.ywh.ywh_caffeine;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.ywh.ywh_caffeine.config.CaffeineHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableApolloConfig
@SpringBootApplication
public class Application {

	private static final Logger logger = LoggerFactory.getLogger(Application.class);
	public static void main(String[] args) {

		SpringApplication.run(Application.class, args);
		//检查caffeine 是否完成初始化
		CaffeineHelper.getCache();
		System.out.println("----- http://localhost:8880/demo/ -----");
	}

}
