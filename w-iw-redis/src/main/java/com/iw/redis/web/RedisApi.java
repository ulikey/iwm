package com.iw.redis.web;

import com.iw.common.core.domain.AjaxResult;
import com.iw.common.core.domain.R;
import com.iw.common.core.domain.redis.RedisObject;
import com.iw.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/redis")
public class RedisApi {
    @Autowired
    private RedisService  redisService;

    @GetMapping("/getVal")
    public R<Object> getVal(@RequestParam(name="key")String key) {
        return R.ok(redisService.getCacheObject(key));
    }

    @PostMapping("/setVal")
    public R<Boolean> setVal(@RequestBody RedisObject redisObject) {
        try {
            redisService.setCacheObject(redisObject.getKey(), redisObject.getVal(), redisObject.getTime(), redisObject.getTimeUnit());
        } catch (Exception e) {
            return R.ok(Boolean.FALSE);
        }
        return R.ok(Boolean.TRUE);
    }

    @GetMapping("/hasKey")
    public R<Boolean> hasKey(@RequestParam(name="key")String key) {
        return R.ok(redisService.hasKey(key));
    }
}
