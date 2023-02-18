package com.iw.gateway.service;

import com.iw.common.core.domain.R;
import com.iw.common.core.domain.redis.RedisObject;
import com.iw.common.core.service.feign.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;

@Component
public class TestClient {
    @Lazy
    @Autowired
    private RedisService redisService;

    @Async // 重点：这里必须在异步线程中执行，执行结果返回Future
    public Future<R<Boolean>> setVal(RedisObject redisObject) {
        R<Boolean> r = redisService.setVal(redisObject);
        return new AsyncResult<>(r);
    }

    @Async // 重点：这里必须在异步线程中执行，执行结果返回Future
    public Future<R<Object>> getVal(String key) {
        R<Object> r = redisService.getVal(key);
        return new AsyncResult<>(r);
    }

    @Async // 重点：这里必须在异步线程中执行，执行结果返回Future
    public Future<R<Boolean>> hasKey(String key) {
        R<Boolean> r = redisService.hasKey(key);
        return new AsyncResult<>(r);
    }
}
