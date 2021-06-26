package com.opefago.lib.cache;

import com.opefago.lib.redis.service.RedisService;
import lombok.ToString;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

@Singleton
@ToString
public class RedisAnnotationCache implements AnnotationCache<String, Object> {
    private static final String TABLE_NAME="kafka-idempotence";

    @Inject
    private RedisService redisService;

    @Override
    public void put(String key, Object value) {
        try {
            redisService.save(TABLE_NAME, key, value, 86400);
        } catch (IOException ignored) {
        }
    }

    @Override
    public Object get(String key) {
        try {
            return redisService.read(TABLE_NAME, key, Object.class);
        } catch (IOException ignored) {
        }
        return null;
    }

    @Override
    public Object evict(String key) {
        redisService.delete(TABLE_NAME, key);
        return null;
    }
}
