package com.iw.common.core.domain.redis;

import lombok.Data;

import java.util.concurrent.TimeUnit;

@Data
public class RedisObject {
    private String key;
    private Object val;
    private Long time;
    private TimeUnit timeUnit;
}
