package com.ywh.ywh_caffeine.config;


import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Data
public class CaffeineConfig {

    //caffeine开关
    @Value("${caffeine.enable:true}")
    private boolean enableCache;

    /**
     * 监控 caffeine开关变化 重置本地缓存
     * @param changeEvent
     */
    @ApolloConfigChangeListener(value = {"application"})
    private void configChangeListener(ConfigChangeEvent changeEvent){
        Set<String> keys = changeEvent.changedKeys();
        for (String key:keys) {
            if(key.equals("caffeine.enable")){
                CaffeineHelper.invalidateAll();
            }
        }
    }
}
