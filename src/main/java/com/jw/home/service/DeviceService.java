package com.jw.home.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jw.home.redis.RedisPublisher;
import com.jw.home.redis.dto.ControlReqMsg;
import com.jw.home.rest.APIServerCaller;
import com.jw.home.websocket.ConnectionManager;
import com.jw.home.websocket.dto.ProtocolType;
import com.jw.home.websocket.dto.WebSocketProtocol;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeviceService {
    private final APIServerCaller apiServerCaller;
    private final ConnectionManager connectionManager;
    private final RedisPublisher redisPublisher;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public boolean isRegisteredDevice(String serial) {
        return apiServerCaller.isRegisteredDevice(serial);
    }

    public void controlDevice(ControlReqMsg controlReqMsg) throws IOException {
        String serial = controlReqMsg.getSerial();
        WebSocketSession session = connectionManager.getSession(serial);
        if (session == null) {
            log.debug("Not exist websocket session : {}", serial);
            return;
        }
        log.debug("Exist session : request through websocket");
        WebSocketProtocol<ControlReqMsg> protocol = new WebSocketProtocol<>();
        protocol.setType(ProtocolType.control);
        protocol.setData(controlReqMsg);
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(protocol)));
    }

    public void notifyControlResult(Map<String, Object> result) {
        redisPublisher.publishDeviceControlResult(result);
    }

    public boolean registerDevice(Map<String, Object> data) {
        return apiServerCaller.registerDevice(data);
    }
}