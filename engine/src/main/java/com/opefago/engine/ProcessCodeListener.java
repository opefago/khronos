package com.opefago.engine;

import com.opefago.lib.common.types.CodeQueue;
import com.opefago.lib.kafka.KafkaListener;
import com.opefago.lib.kafka.annotations.KafkaTopicListener;
import com.opefago.lib.models.RunCode;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Singleton;

@KafkaListener
@Singleton
@Slf4j
public class ProcessCodeListener {

    @KafkaTopicListener(topic = CodeQueue.POST_CODE_TO_COMPILER)
    public void handleCode(RunCode runCode){
        log.info("Running code: {}", runCode);
    }
}
