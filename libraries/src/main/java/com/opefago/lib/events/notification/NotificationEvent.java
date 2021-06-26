package com.opefago.lib.events.notification;

import com.opefago.lib.events.Event;
import com.opefago.lib.events.notification.types.NotificationEventType;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class NotificationEvent implements Event {
    private NotificationEventType eventType;
    private NotificationData data;
}
