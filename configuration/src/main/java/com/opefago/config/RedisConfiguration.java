package com.opefago.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import lombok.Getter;
import lombok.NonNull;

@Getter
@NonNull
public class RedisConfiguration extends Configuration {
    @JsonProperty("host") private String host;
    @JsonProperty("pubSubChannel") private String pubSubChannel;
    @JsonProperty("port") private Integer port;
    @JsonProperty("db") private Integer db;
    @JsonProperty("timeout") private Integer timeout;
    @JsonProperty("maxIdle") private Integer maxIdle;
    @JsonProperty("minIdle") private Integer minIdle;
    @JsonProperty("maxTotal") private Integer maxTotal;
    @JsonProperty("ssl") private Boolean ssl;
    @JsonProperty("retryCount") private Integer retryCount;
    @JsonProperty("ttl") private Integer ttl;
    @JsonProperty("pingConnectionInterval") private Integer pingConnectionInterval;
}
