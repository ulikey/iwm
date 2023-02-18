package com.iw.common.core.service.feign;

import com.iw.common.core.constant.ServiceNameConstants;
import com.iw.common.core.domain.R;
import com.iw.common.core.domain.redis.RedisObject;
import com.iw.common.core.service.RedisFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用户服务
 * 
 * @author ruoyi
 */
@FeignClient(contextId = "redisService", value = ServiceNameConstants.REDIS_SERVICE, fallbackFactory = RedisFallbackFactory.class)
public interface RedisService {
    @GetMapping("/redis/getVal")
    R<Object> getVal(@RequestParam(name="key")String key);

    @PostMapping("/redis/setVal")
    R<Boolean> setVal(@RequestBody RedisObject redisObject);

    @GetMapping("/redis/hasKey")
    public R<Boolean> hasKey(@RequestParam(name="key")String key);
}
