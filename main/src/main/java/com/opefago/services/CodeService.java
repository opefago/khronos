package com.opefago.services;

import com.opefago.config.CodeConfiguration;
import com.opefago.configuration.AppConfiguration;
import com.opefago.lib.common.types.CodeQueue;
import com.opefago.lib.common.types.Status;
import com.opefago.lib.kafka.KafkaProducerApi;
import com.opefago.lib.models.CodeStatus;
import com.opefago.lib.models.RunCode;
import com.opefago.lib.models.RunCodeResponse;
import com.opefago.lib.models.command.RunCodeCommand;
import com.opefago.lib.redis.service.RedisService;
import org.apache.http.client.HttpResponseException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.UUID;

@Singleton
public class CodeService {
    final KafkaProducerApi kafkaProducerApi;
    final RedisService redisService;
    final CodeConfiguration codeConfiguration;
    @Inject
    public CodeService(
            final KafkaProducerApi kafkaProducerApi,
            final RedisService redisService,
            final AppConfiguration appConfiguration
            ){
        this.kafkaProducerApi = kafkaProducerApi;
        this.redisService = redisService;
        this.codeConfiguration = appConfiguration.getCodeConfiguration();
    }

    public CodeStatus getCodeStatus(final UUID codeId) throws HttpResponseException {
        if(redisService.exists(codeId.toString())){
            try {
                return redisService.read(codeId.toString(), CodeStatus.class);
            } catch (IOException e) {
                throw new HttpResponseException(404, String.format("Error checking code %s", codeId));
            }
        }
        throw new HttpResponseException(404, String.format("Cannot find status for code %s", codeId));
    }

    public RunCodeResponse publishCode(final RunCodeCommand command) throws HttpResponseException{
        final UUID codeId = UUID.randomUUID();
        CodeStatus status = new CodeStatus(Status.NEW, null);
        try {
            redisService.save(codeId.toString(), status, codeConfiguration.getExpirationInSeconds());
        } catch (IOException e) {
            throw new HttpResponseException(404, String.format("Error processing code %s", codeId));
        }
        kafkaProducerApi.send(CodeQueue.POST_CODE_TO_COMPILER,
                RunCode.builder()
                        .codeId(codeId)
                        .lang(command.getLang())
                        .source(command.getSource()).build()
        );
        return new RunCodeResponse(codeId);
    }
}
