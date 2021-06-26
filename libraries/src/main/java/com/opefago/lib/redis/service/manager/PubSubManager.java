package com.opefago.lib.redis.service.manager;

import com.opefago.lib.events.notification.NotificationData;
import com.opefago.lib.redis.interfaces.PubSubListener;
import com.opefago.lib.redis.service.RedisService;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Singleton
public class PubSubManager {
    private final Map<String, PubSubListener> listeners = new ConcurrentHashMap<>();
    private final Executor executor      = Executors.newCachedThreadPool();
    private final RedissonClient client;

    @Inject
    public PubSubManager(final RedisService redisService){
        client = redisService.getClient();
    }

    public synchronized void subscribe(final String topic, final PubSubListener listener) {
        listeners.put(topic, listener);
        RTopic kafkaTopic = client.getTopic(topic);
        kafkaTopic.addListener(NotificationData.class, (charSequence, bytes) ->
                listener.onMessage(bytes)
        );
        listener.onSubscribe(topic);
    }

    public boolean hasListener(final String topic){
        return listeners.containsKey(topic);
    }

    public synchronized void unsubscribe(String topic, PubSubListener listener) {
        PubSubListener pubSubListener = listeners.get(topic);
        if (Objects.nonNull(pubSubListener) && pubSubListener == listener) {
            listeners.remove(topic);
            client.getTopic(topic).removeAllListeners();
        }
        listener.onUnsubscribe(topic);
    }

    public void sendMessage(final String topic, NotificationData message){
        executor.execute(()->{
            RTopic redisTopic = client.getTopic(topic);
            redisTopic.publish(message);
        });
    }
}
