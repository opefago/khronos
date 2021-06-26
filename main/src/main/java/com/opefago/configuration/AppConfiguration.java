package com.opefago.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.opefago.config.CodeConfiguration;
import com.opefago.config.RedisConfiguration;
import com.opefago.lib.kafka.KafkaConsumerConfiguration;
import com.opefago.lib.kafka.KafkaProducerConfiguration;
import io.dropwizard.Configuration;
import lombok.Getter;


@Getter
public class AppConfiguration extends Configuration {
    @JsonProperty("redis") private RedisConfiguration redisConfiguration;
    @JsonProperty("consumer") private KafkaConsumerConfiguration kafkaConsumerConfiguration;
    @JsonProperty("producer") private KafkaProducerConfiguration kafkaProducerConfiguration;
    @JsonProperty("code") private CodeConfiguration codeConfiguration;
}
