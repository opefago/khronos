package com.opefago.lib.kafka;

import io.dropwizard.util.Duration;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Properties;

@Singleton
public class KafkaProducerApi {
    private final KafkaProducerConfiguration kafkaProducerConfiguration;
    private final KafkaProducer<String, Object> kafkaProducer;

    @Inject
    public KafkaProducerApi(final KafkaProducerConfiguration kafkaProducerConfiguration){
        this.kafkaProducerConfiguration = kafkaProducerConfiguration;
        kafkaProducer = new KafkaProducer<>(getProperty());
    }

    private Properties getProperty(){
        Properties props = new Properties();
        props.put("bootstrap.servers", kafkaProducerConfiguration.getBootstrapServer());
        props.put("acks", kafkaProducerConfiguration.getAcks().orElse(""));
        props.put("retries", kafkaProducerConfiguration.getRetries().orElse(0));
        props.put("batch.size", kafkaProducerConfiguration.getBatchSize());
        props.put("linger.ms", (int)kafkaProducerConfiguration.getLinger().toMilliseconds());
        props.put("buffer.memory", kafkaProducerConfiguration.getBufferMemory());
        props.put("key.serializer", kafkaProducerConfiguration.getSerializer());
        props.put("value.serializer", kafkaProducerConfiguration.getSerializer());
        props.put("max.in.flight.requests.per.connection", kafkaProducerConfiguration.getMaxInFlightRequestsPerConnection().orElse(1));
        props.put("max.block.ms", (kafkaProducerConfiguration.getMaxPollBlockTime().orElse(Duration.milliseconds(1L))).toMilliseconds());
        props.put("compression.type", kafkaProducerConfiguration.getCompressionType());
        props.put("send.buffer.bytes", kafkaProducerConfiguration.getSendBufferBytes());
        props.put("receive.buffer.bytes", kafkaProducerConfiguration.getReceiveBufferBytes());
        props.put("request.timeout.ms", (int)kafkaProducerConfiguration.getRequestTimeout().toMilliseconds());
        props.put("enable.idempotence", kafkaProducerConfiguration.isEnableIdempotence());
        if (kafkaProducerConfiguration.getTransactionalId().isPresent()) {
            props.put("transactional.id", kafkaProducerConfiguration.getTransactionalId().get());
        }

        return props;
    }

    public void send(String topic, Object message){
        kafkaProducer.send(new ProducerRecord<>(topic, "0", message));
    }
    public void send(String topic, String key, Object message){
        kafkaProducer.send(new ProducerRecord<>(topic, key, message));
    }
}
