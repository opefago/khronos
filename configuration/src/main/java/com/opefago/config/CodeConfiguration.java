package com.opefago.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class CodeConfiguration {
    @JsonProperty("expirationInSeconds") private int expirationInSeconds;
}
