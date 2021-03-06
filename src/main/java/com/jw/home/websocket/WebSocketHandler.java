package com.jw.home.websocket;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jw.home.domain.TriggerType;
import com.jw.home.service.DeviceService;
import com.jw.home.websocket.dto.WebSocketProtocol;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequiredArgsConstructor
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {
    private final ConnectionManager connectionManager;
    private final DeviceService deviceService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    // < Serial, Session > TODO 배치 삭제
    private final ConcurrentHashMap<String, WebSocketSession> tempSessions = new ConcurrentHashMap<>(); // 기기 연결 이전에 임시 세션 저장

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // (ex) ws://localhost:9093/websocket/connect?serial=01280001s
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
        if (connectionManager.existSessionInfo(serial)) {
            log.warn("device {} session is already existed.", serial);
            session.close();
            return;
        }
        session.getAttributes().put("serial", serial);
        String deviceId = deviceService.getDeviceId(serial);
        /*
         * deviceId 가 null 이 아니면 사용자에게 정상 등록된 디바이스임.
         * 네트워크 문제등으로 연결이 끊어졌다가 다시 연결 요청이 온 경우
         * TODO 서버 재기동시와 구분할 수 있는 방법이 있는지..
         */
        if (deviceId != null) {
            connectionManager.putSessionInfo(serial, new SessionInfo(deviceId, session));
            deviceService.notifyChangeConnection(connectionManager.getSessionInfo(serial).getDeviceId(), serial, true);
        } else {
            tempSessions.put(serial, session);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String serial = (String) session.getAttributes().get("serial");
        log.info("[closed connection] sessionId : {}, deviceSerial : {}", session.getId(), serial);

        SessionInfo sessionInfo = connectionManager.getSessionInfo(serial);
        if (sessionInfo != null) {
            deviceService.notifyChangeConnection(sessionInfo.getDeviceId(), serial, false);
            connectionManager.removeSessionInfo(serial);
        }
        tempSessions.remove(serial);
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
                String deviceId = deviceService.registerDevice(data);
                if (deviceId != null) {
                    connectionManager.putSessionInfo(serial, new SessionInfo(deviceId, session));
                    tempSessions.remove(serial);
                }
                break;
            case controlResult:
                deviceService.notifyControlResult(data);
                break;
            case changeState:
                TriggerType trigger = TriggerType.valueOf((String) data.get("trigger"));
                @SuppressWarnings("unchecked")
                Map<String, Object> states = (Map<String, Object>) data.get("states");
                deviceService.notifyChangeState(trigger,
                        connectionManager.getSessionInfo(serial).getDeviceId(),
                        serial, states);
                break;
            default:
                break;
        }
    }
}
