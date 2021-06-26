package com.opefago.lib.redis.service.manager;


import com.opefago.lib.redis.service.RedisService;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

@Singleton
public class CacheManager {
    final RedisService redisService;
    @Inject
    public CacheManager(final RedisService redisService){
        this.redisService = redisService;
    }

    public <T>T get(final String cacheName, final String key, final Class<T> tClass)
            throws IOException {
        return redisService.read(cacheName, key, tClass);
    }

    public void put(final String cacheName, final String key, final Object value)
            throws IOException {
        redisService.save(cacheName, key, value);
    }

    public void evict(final String cacheName, final String key){
        redisService.delete(cacheName, key);
    }
}
