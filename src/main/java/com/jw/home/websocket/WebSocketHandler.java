package com.jw.home.websocket;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jw.home.rest.APIServerCaller;
import com.jw.home.rest.AsyncResponseManager;
import com.jw.home.service.DeviceService;
import com.jw.home.websocket.dto.WebSocketProtocol;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {
    @Autowired
    private APIServerCaller apiServerCaller;
    @Autowired
    private ConnectionManager connectionManager;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private AsyncResponseManager asyncResponseManager;

    private final ObjectMapper objectMapper = new ObjectMapper();
    // < Serial, Session > TODO 배치 삭제
    private final ConcurrentHashMap<String, WebSocketSession> tempSessions = new ConcurrentHashMap<>(); // 기기 연결 이전에 임시 세션 저장

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("[established connection] sessionId : {}", session.getId());
        String rawQuery = Objects.requireNonNull(session.getUri()).getRawQuery();
        if (rawQuery == null) {
            session.close();
            return;
        }
        String serial = Arrays.stream(rawQuery.split("&")).filter(s -> s.startsWith("serial="))
                .findFirst().map(s -> s.substring(7)).orElse(null);
        log.info("device serial : {}", serial);
        if (serial == null) {
            session.close();
            return;
        }
        // sessions 에 serial 에 해당하는 session 이 있으면 요청 session 바로 해제.
        if (connectionManager.getSession(serial) != null) {
            log.warn("device {} session is already existed.", serial);
            session.close();
            return;
        }
        session.getAttributes().put("serial", serial);

        if (deviceService.isRegisteredDevice(serial)) {
            connectionManager.addSession(serial, session);
        } else {
            tempSessions.put(serial, session);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String deviceSerial = (String) session.getAttributes().get("serial");
        log.info("[closed connection] sessionId : {}, deviceSerial : {}", session.getId(), deviceSerial);

        connectionManager.removeSession(deviceSerial);
        tempSessions.remove(deviceSerial);

        // TODO DB update (device status to offline)
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.debug(message.getPayload());
        WebSocketProtocol<Map<String, Object>> payload =
                objectMapper.readValue(message.getPayload(), new TypeReference<>() {});
        String serial = (String) session.getAttributes().get("serial");
        Map<String, Object> data = payload.getData();

        switch (payload.getType()) {
            case register:
                // TODO Get access_token to call API server
                if (!serial.equals(data.get("serial"))) {
                    log.warn("Not match device serial : {} - {}", serial, data.get("serial"));
                    return;
                }
                boolean result = apiServerCaller.registerDevice(data);
                if (result) {
                    connectionManager.addSession(serial, session);
                    tempSessions.remove(serial);
                }
                break;
            case controlResult:
                String transactionId = payload.getTransactionId();
                asyncResponseManager.sendResult(transactionId, data);
                break;
            default:
                break;
        }
    }
}
