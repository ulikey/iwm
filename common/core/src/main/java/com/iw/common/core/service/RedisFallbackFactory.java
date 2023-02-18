package com.iw.common.core.service;

import com.iw.common.core.domain.R;
import com.iw.common.core.domain.redis.RedisObject;
import com.iw.common.core.service.feign.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Component
public class RedisFallbackFactory implements FallbackFactory<RedisService> {

    @Override
    public RedisService create(Throwable throwable) {
        log.error("redis服务调用失败:{}", throwable.getMessage());
        return new RedisService() {
            @Override
            public R<Object> getVal(@RequestParam(name="key")String key) {
                log.error("34525222");
                return R.fail("getVal Fail:" + throwable.getMessage());
            }

            @Override
            public R<Boolean> setVal(@RequestBody RedisObject redisObject) {
                return R.fail("setVal Fail:" + throwable.getMessage());
            }

            @Override
            public R<Boolean> hasKey(@RequestParam(name="key")String key) {
                return R.fail("hasKey Fail:" + throwable.getMessage());
            }
        };
    }
}
