package com.opefago.engine.executors;

import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.StreamType;
import com.github.dockerjava.core.InvocationBuilder;
import com.opefago.lib.common.types.Language;
import com.opefago.lib.models.RunCode;
import com.opefago.lib.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Singleton
@Slf4j
public class Java8Executor extends Executor{
    @Inject
    public Java8Executor(final RedisService redisService) {
        super(redisService);
    }

    @Override
    public boolean supports(Language language) {
        return language == Language.JAVA_8;
    }

    @Override
    public boolean execute(RunCode runCode) throws InterruptedException, IOException {
        final String image = "openjdk";
        final String tag = "8";
        pullImage(image, tag);
        final String parentName = runCode.getCodeId().toString().replace("-", "");
        File solutionFile = new File(
                String.format("%s%s%s", parentName, File.separator, "Solution.java")
        );
        final boolean isCreated = solutionFile.getParentFile().mkdirs();
        FileWriter solutionWriter = new FileWriter(solutionFile);
        solutionWriter.write(runCode.getSource());
        solutionWriter.close();
        String containerId = createAndStartContainer(String.format("%s:%s",image, tag), solutionFile);
        String[] command = {"javac", "app/Solution.java"};

        ExecCreateCmdResponse response = docker.execCreateCmd(containerId)
                .withCmd(command)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .exec();

        InvocationBuilder.AsyncResultCallback<Frame> resultCallback = docker
                .execStartCmd(response.getId())
                .withDetach(false)
                .exec(new InvocationBuilder.AsyncResultCallback<>());

        Frame result = resultCallback.awaitResult();
        if(result != null) {

            if (result.getStreamType() == StreamType.STDERR)
                log.info("Result returned error {}", new String(result.getPayload()));
            else
                log.info("Result returned success {}", new String(result.getPayload()));
        }else{
            command = new String[]{"java", "-cp", "app", "Solution"};
            response = docker.execCreateCmd(containerId)
                    .withCmd(command)
                    .withAttachStdout(true)
                    .withAttachStderr(true)
                    .exec();

            resultCallback = docker
                    .execStartCmd(response.getId())
                    .withDetach(false)
                    .exec(new InvocationBuilder.AsyncResultCallback<>());

            result = resultCallback.awaitResult();

            if(result != null) {

                if (result.getStreamType() == StreamType.STDERR)
                    log.info("Result returned error {}", new String(result.getPayload()));
                else
                    log.info("Result returned success {}", new String(result.getPayload()));
            }
        }

        killAndRemoveContainer(containerId);
        return false;
    }
}
