package com.opefago.lib.redis.service.queue;

import com.opefago.lib.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.TimeUnit;

@Singleton
@Slf4j
public class TimeDelayQueue {
    final RedissonClient client;

    @Inject
    public TimeDelayQueue(final RedisService redisService){
        client = redisService.getClient();
    }

    public <T> T take(final String queueName){
        RBlockingQueue<T> destinationQueue = client.getBlockingQueue(queueName);
        RDelayedQueue<T> delayedQueue = client.getDelayedQueue(destinationQueue);
        try {
            return destinationQueue.take();
        } catch (InterruptedException e) {
            log.info("Error {}", e.getMessage(), e);
            return null;
        }
    }

    public <T> void add(final String queueName, T item, long delay, TimeUnit timeUnit){
        RBlockingQueue<T> fairQueue = client.getBlockingQueue(queueName);
        RDelayedQueue<T> delayedQueue = client.getDelayedQueue(fairQueue);
        delayedQueue.offerAsync(item, delay, timeUnit);
    }

}
