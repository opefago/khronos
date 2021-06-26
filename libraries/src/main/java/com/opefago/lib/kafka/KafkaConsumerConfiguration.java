package com.opefago.lib.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.util.Duration;
import lombok.Getter;
import org.apache.kafka.common.serialization.StringDeserializer;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@Getter
@NotNull
public class KafkaConsumerConfiguration {
    @NotEmpty
    @JsonProperty
    private String bootstrapServer;
    @NotEmpty
    @JsonProperty
    private String consumerGroupId;
    private String deserializer = StringDeserializer.class.getName();
    @JsonProperty
    private boolean autoCommitEnabled = true;
    @JsonProperty
    private Duration autoCommitInterval = Duration.seconds(5L);
    @Min(-1L)
    @JsonProperty
    private int sendBufferBytes = -1;
    @Min(-1L)
    @JsonProperty
    private int receiveBufferBytes = -1;
    @Min(1L)
    @JsonProperty
    private int maxPollRecords = 500;
    @NotNull
    @JsonProperty
    private Duration maxPollInterval = Duration.minutes(5L);
}
