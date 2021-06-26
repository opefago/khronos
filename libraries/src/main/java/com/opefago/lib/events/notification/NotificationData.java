package com.opefago.lib.events.notification;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opefago.lib.events.notification.types.NotificationEventType;
import com.opefago.lib.util.MapperUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class NotificationData implements Serializable {
    public static final String USER_ID="userId";
    public static final String PHONE_NUMBER="phoneNumber";
    public static final String NOTIFICATION_ID="notificationId";
    public static final String EMAIL="email";
    public static final String MESSAGE="message";
    public static final String TITLE="title";
    public static final String OTP="otp";
    public static final String REFERENCE="reference";

    protected Map<String, String> data;
    protected NotificationEventType eventType;

    public static NotificationData parseMessage(byte[] message)
            throws JsonProcessingException {
        ObjectMapper mapper = MapperUtil.INSTANCE.getObjectMapper();
        return mapper.readValue(new String(message), NotificationData.class);
    }
}
