package com.opefago.lib.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.opefago.lib.kafka.serializer.KafkaJsonSerializer;
import io.dropwizard.util.Duration;
import io.dropwizard.validation.MinDuration;
import lombok.Getter;
import org.apache.kafka.common.record.CompressionType;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Getter
@NotNull
public class KafkaProducerConfiguration {
    @NotEmpty
    @JsonProperty
    private String bootstrapServer;
    @JsonProperty
    private Optional<String> acks = Optional.empty();
    @JsonProperty
    private Optional<Integer> retries = Optional.empty();
    @JsonProperty
    private Optional<Integer> maxInFlightRequestsPerConnection = Optional.empty();
    @JsonProperty
    private Optional<Duration> maxPollBlockTime = Optional.empty();
    @NotEmpty
    @JsonProperty
    private String compressionType = CompressionType.GZIP.name;

    @NotEmpty
    @JsonProperty
    private String serializer = KafkaJsonSerializer.class.getName();
    @Min(-1L)
    @JsonProperty
    private int sendBufferBytes = -1;
    @Min(-1L)
    @JsonProperty
    private int receiveBufferBytes = -1;
    @Min(0L)
    @JsonProperty
    private long bufferMemory = 33554432L;
    @Min(0L)
    @JsonProperty
    private int batchSize = 16384;
    @MinDuration(0L)
    @JsonProperty
    private Duration linger = Duration.milliseconds(0L);;
    @MinDuration(0L)
    @JsonProperty
    private Duration requestTimeout = Duration.seconds(30L);
    @JsonProperty
    private boolean enableIdempotence = false;
    @JsonProperty
    private Optional<String> transactionalId = Optional.empty();
}
