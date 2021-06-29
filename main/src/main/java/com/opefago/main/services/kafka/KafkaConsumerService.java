package com.opefago.main.services.kafka;

import com.opefago.lib.kafka.KafkaConsumerListenerFactory;
import io.dropwizard.lifecycle.Managed;

import javax.inject.Inject;

public class KafkaConsumerService implements Managed {
    private final KafkaConsumerListenerFactory kafkaConsumerListenerFactory;

    @Inject
    public KafkaConsumerService(final KafkaConsumerListenerFactory kafkaConsumerListenerFactory){
        this.kafkaConsumerListenerFactory = kafkaConsumerListenerFactory;
    }
    @Override
    public void start() throws Exception {
        if(kafkaConsumerListenerFactory != null)
            kafkaConsumerListenerFactory.start();
    }

    @Override
    public void stop() throws Exception {
         if(kafkaConsumerListenerFactory != null)
            kafkaConsumerListenerFactory.stop();
    }
}
