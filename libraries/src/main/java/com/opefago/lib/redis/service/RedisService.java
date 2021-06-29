package com.opefago.lib.redis.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opefago.config.RedisConfiguration;
import io.dropwizard.lifecycle.Managed;
import lombok.Getter;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Singleton
public class RedisService implements Managed {
    @Getter
    private RedissonClient client;
    final ObjectMapper mapper = new ObjectMapper();
    final private String defaultTable;

    @Inject
    public RedisService(final RedisConfiguration redisConfiguration) {
        Config config = new Config();
        final String address;
        if(redisConfiguration.getSsl()){
            address = String.format("rediss://%s:%s", redisConfiguration.getHost(), redisConfiguration.getPort());
        }else{
            address = String.format("redis://%s:%s", redisConfiguration.getHost(), redisConfiguration.getPort());
        }
        config.useSingleServer()
                .setAddress(address)
                .setConnectionMinimumIdleSize(redisConfiguration.getMinIdle())
                .setSubscriptionConnectionMinimumIdleSize(redisConfiguration.getMinIdle())
                .setIdleConnectionTimeout(redisConfiguration.getTimeout())
                .setPingConnectionInterval(redisConfiguration.getPingConnectionInterval())
                .setDatabase(redisConfiguration.getDb());
        client = Redisson.create(config);
        defaultTable = "khronos";
    }

    public <T> T read(final String key, final Class<T> tClass) throws IOException {
       return read(defaultTable, key, tClass);
    }

    public <T> T read(final String tableName, final String key, final Class<T> tClass) throws IOException {
        final Object payload = client.getMapCache(tableName).get(key);
        if (Objects.isNull(payload)) {
            return null;
        }
        return mapper.readValue((String)payload, tClass);
    }

    public <T> T read(final String key, final TypeReference<T> typeReference) throws IOException {
        return read(defaultTable, key, typeReference);

    }
    public <T> T read(final String tableName, final String key, final TypeReference<T> typeReference) throws IOException {
        final Object payload = client.getMapCache(tableName).get(key);
        if (Objects.isNull(payload)) {
            return null;
        }
        return mapper.readValue((String)payload, typeReference);

    }

    public void delete(final String key) {
        delete(defaultTable, key);
    }

    public void delete(final String tableName, final String key) {
        client.getMapCache(tableName).fastRemove(key);
    }

    public void save(final String key, final Object value, final long expirationInSeconds) throws IOException {
        save(defaultTable, key, value, expirationInSeconds);
    }

    public void save(final String tableName, final String key, final Object value, final long expirationInSeconds) throws IOException {
        client.getMapCache(tableName).put(key, mapper.writeValueAsString(value), expirationInSeconds, TimeUnit.SECONDS);
    }

    public void save(final String key, final Object value) throws IOException {
        save(defaultTable, key, value);
    }

    public void save(final String tableName, final String key, final Object value) throws IOException {
        client.getMapCache(tableName).fastPut(key, mapper.writeValueAsString(value));
    }

    public boolean exists(final String key) {
        return exists(defaultTable, key);
    }

    public boolean exists(final String tableName, final String key) {
        return client.getMapCache(tableName).containsKey(key);
    }

    @Override
    public void start() throws Exception {

    }

    public void stop() {
        client.shutdown();
    }
}