package com.jw.home.websocket.dto;

import lombok.Data;

import java.util.Map;

@Data
public class WebSocketProtocol {
    private ProtocolType type;
    private Map<String, Object> data;
}
