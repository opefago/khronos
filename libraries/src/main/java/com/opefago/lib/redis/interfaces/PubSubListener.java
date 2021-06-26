package com.opefago.lib.redis.interfaces;

import com.opefago.lib.events.notification.NotificationData;

public interface PubSubListener {
    void onSubscribe(String topic);
    void onUnsubscribe(String topic);
    void onMessage(NotificationData message);
}
