package com.opefago.lib.kafka.deserialiser;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class KafkaJsonDeserializer<T> implements Deserializer<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaJsonDeserializer.class);
    private Class<T> tClass;
    public static final String CONFIG_KEY_CLASS = "value.deserializer.class";

    public KafkaJsonDeserializer() {
    }

    public void configure(Map map, boolean b) {
        String className = String.valueOf(map.get("value.deserializer.class"));

        try {
            this.tClass = (Class<T>) Class.forName(className);
        } catch (ClassNotFoundException var5) {
            var5.printStackTrace();
        }

    }

    public T deserialize(String s, byte[] bytes) {
        ObjectMapper mapper = new ObjectMapper();
        T obj = null;

        try {
            obj = mapper.readValue(bytes, this.tClass);
        } catch (Exception var6) {
            LOGGER.error(var6.getMessage());
        }

        return obj;
    }

    public void close() {
    }
}
