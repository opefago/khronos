package com.opefago.engine.executors;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.opefago.lib.common.types.Language;
import com.opefago.lib.models.RunCode;
import com.opefago.lib.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.github.dockerjava.api.model.HostConfig.newHostConfig;

@Slf4j
public abstract class Executor {
    protected final RedisService redisService;
    protected DockerClient docker;

    public Executor(final RedisService redisService){
        this.redisService = redisService;
        init();
    }

    private void init(){
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .build();

        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();
        docker = DockerClientImpl.getInstance(config, httpClient);

    }
    protected void pullImage(final String imageName, final String tag) throws InterruptedException {
        List images = docker.listImagesCmd()
                .withImageNameFilter(String.format("%s:%s", imageName, tag))
                .exec();
        if (images.isEmpty()) {
            docker.pullImageCmd(imageName)
                    .withTag(tag)
                    .exec(new PullImageResultCallback())
                    .awaitCompletion(30, TimeUnit.SECONDS);
        }

    }

    protected String createAndStartContainer(final String imageName, final File file){
        Volume volume = new Volume("/app");

        HostConfig config = HostConfig.newHostConfig()
                .withBinds(new Bind(file.getParentFile().getAbsolutePath(), volume, AccessMode.rw));

        CreateContainerCmd createContainerCmd = docker.createContainerCmd(imageName)
                .withHostConfig(config)
                .withTty(true);

        CreateContainerResponse container
                = createContainerCmd.exec();

        docker.startContainerCmd(container.getId()).exec();
        return container.getId();
    }

    protected void killAndRemoveContainer(String containerId){
        InspectContainerResponse.ContainerState state = docker
                .inspectContainerCmd(containerId).exec().getState();
        if(state != null && state.getRunning() != null && state.getRunning()) {
            docker.stopContainerCmd(containerId).exec();
        }
        docker.removeContainerCmd(containerId).exec();
    }

    public abstract boolean supports(Language language);
    public abstract boolean execute(final RunCode runCode) throws InterruptedException, IOException ;
}
