package com.opefago.configuration;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.opefago.config.RedisConfiguration;
import com.opefago.lib.kafka.KafkaConsumerConfiguration;
import com.opefago.lib.kafka.KafkaProducerConfiguration;

public class ConfigModule extends AbstractModule {
    @Provides
    public RedisConfiguration getRedisConfiguration(AppConfiguration appConfiguration){
        return appConfiguration.getRedisConfiguration();
    }

    @Provides
    public KafkaConsumerConfiguration getKafkaConsumerConfiguration(AppConfiguration appConfiguration){
        return appConfiguration.getKafkaConsumerConfiguration();
    }

    @Provides
    public KafkaProducerConfiguration getKafkaProducerConfiguration(AppConfiguration appConfiguration){
        return appConfiguration.getKafkaProducerConfiguration();
    }
}
