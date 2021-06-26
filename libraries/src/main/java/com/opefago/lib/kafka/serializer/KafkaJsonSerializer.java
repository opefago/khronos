package com.opefago.lib.kafka.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class KafkaJsonSerializer<T> implements Serializer<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaJsonSerializer.class);

    public KafkaJsonSerializer() {
    }

    public void configure(Map map, boolean b) {
    }

    public byte[] serialize(String s, Object o) {
        byte[] retVal = null;
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            retVal = objectMapper.writeValueAsBytes(o);
        } catch (Exception var6) {
            LOGGER.error(var6.getMessage());
        }

        return retVal;
    }

    public void close() {
    }
}
