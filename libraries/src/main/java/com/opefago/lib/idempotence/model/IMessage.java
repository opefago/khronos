package com.opefago.lib.idempotence.model;

public interface IMessage {
    Object getId();
    String serialize();
}
