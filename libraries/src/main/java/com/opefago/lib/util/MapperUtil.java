package com.opefago.lib.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public enum MapperUtil {
    INSTANCE;
    private final ObjectMapper mapper = new ObjectMapper();

    private MapperUtil(){
        // Perform any configuration on the ObjectMapper here.
    }

    public ObjectMapper getObjectMapper() {
        return mapper;
    }
}
