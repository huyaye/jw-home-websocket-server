package com.jw.home.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jw.home.exception.WebSocketSessionException;
import com.jw.home.rest.APIServerCaller;
import com.jw.home.rest.dto.ControlDeviceReq;
import com.jw.home.websocket.ConnectionManager;
import com.jw.home.websocket.dto.ProtocolType;
import com.jw.home.websocket.dto.WebSocketProtocol;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final ObjectMapper objectMapper = new ObjectMapper();

    public boolean isRegisteredDevice(String serial) {
        return apiServerCaller.isRegisteredDevice(serial);
    }

    public void controlDevice(ControlDeviceReq request, String transactionId) throws WebSocketSessionException {
        String serial = request.getSerial();
        WebSocketSession session = connectionManager.getSession(serial);
        if (session == null) {
            log.warn("Not found websocket session : {}", serial);
            throw WebSocketSessionException.INSTANCE;
        }
        try {
            WebSocketProtocol<ControlDeviceReq> protocol = new WebSocketProtocol<>();
            protocol.setType(ProtocolType.control);
            protocol.setTransactionId(transactionId);
            protocol.setData(request);
            TextMessage message = new TextMessage(objectMapper.writeValueAsString(protocol));
            session.sendMessage(message);
        } catch (IOException e) {
            log.warn("Session send message exception", e);
            throw WebSocketSessionException.INSTANCE;
        }
    }

    public boolean registerDevice(Map<String, Object> data) {
        return apiServerCaller.registerDevice(data);
    }
}