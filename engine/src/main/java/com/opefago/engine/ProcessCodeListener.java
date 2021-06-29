package com.opefago.engine;

import com.opefago.engine.executors.Java8Executor;
import com.opefago.lib.common.types.CodeQueue;
import com.opefago.lib.kafka.KafkaListener;
import com.opefago.lib.kafka.annotations.KafkaTopicListener;
import com.opefago.lib.models.RunCode;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.io.IOException;

@KafkaListener
@Slf4j
public class ProcessCodeListener {
    final Java8Executor java8Executor;
    @Inject
    public ProcessCodeListener(final Java8Executor java8Executor){
        this.java8Executor = java8Executor;
    }

    @KafkaTopicListener(topic = CodeQueue.POST_CODE_TO_COMPILER)
    public void handleCode(RunCode runCode){
        log.info("Running code: {}", runCode);
        try {
            java8Executor.execute(runCode);
        } catch ( InterruptedException | IOException e) {
            e.printStackTrace();
            log.info(e.getMessage());
        }

    }
}
