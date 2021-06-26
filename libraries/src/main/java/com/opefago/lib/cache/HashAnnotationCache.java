package com.opefago.lib.cache;

import lombok.ToString;

import javax.inject.Singleton;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
@ToString
public class HashAnnotationCache implements AnnotationCache<String, String> {

    ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();

    @Override
    public void put(String key, String value) {
        cache.put(key, value);
    }

    @Override
    public String get(String key) {
        return cache.get(key);
    }

    @Override
    public String evict(String key) {
        return cache.remove(key);
    }

}
