package com.jw.home.websocket.dto;

import lombok.Data;

import java.util.Map;

@Data
public class WebSocketProtocol<T> {
    private ProtocolType type;
    private String transactionId;
    private T data;
}
