package com.opefago.lib.idempotence.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.opefago.lib.cache.AnnotationCache;
import com.opefago.lib.cache.RedisAnnotationCache;

public class IdempotentConfigModule extends AbstractModule {
    final AnnotationCache<String, Object> cache = new RedisAnnotationCache();
    @Provides
    public AnnotationCache<String, Object> getIdempotentCache(){
        return cache;
    }

    @Override
    public void configure() {
        requestInjection(cache);
    }
}
