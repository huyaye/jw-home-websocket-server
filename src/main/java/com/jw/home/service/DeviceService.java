package com.jw.home.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jw.home.domain.TriggerType;
import com.jw.home.kafka.KafkaProducer;
import com.jw.home.kafka.dto.DeviceStateValue;
import com.jw.home.redis.RedisPublisher;
import com.jw.home.redis.dto.ControlReqMsg;
import com.jw.home.client.APIServerCaller;
import com.jw.home.websocket.ConnectionManager;
import com.jw.home.websocket.SessionInfo;
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
    private final KafkaProducer kafkaProducer;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getDeviceId(String serial) {
        return apiServerCaller.getDeviceId(serial);
    }

    public void controlDevice(ControlReqMsg controlReqMsg) throws IOException {
        String serial = controlReqMsg.getSerial();
        SessionInfo sessionInfo = connectionManager.getSessionInfo(serial);
        if (sessionInfo == null) {
            log.debug("Not exist websocket session : {}", serial);
            return;
        }
        WebSocketSession session = sessionInfo.getSession();
        log.debug("Exist session : request through websocket");
        WebSocketProtocol<ControlReqMsg> protocol = new WebSocketProtocol<>();
        protocol.setType(ProtocolType.control);
        protocol.setData(controlReqMsg);
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(protocol)));
    }

    public void notifyControlResult(Map<String, Object> result) {
        redisPublisher.publishDeviceControlResult(result);
    }

    public String registerDevice(Map<String, Object> data) {
        return apiServerCaller.registerDevice(data);
    }

    public void notifyChangeState(TriggerType trigger, String deviceId, String serial, Map<String, Object> states) {
        DeviceStateValue value = DeviceStateValue.builder()
                .connection("websocket")
                .trigger(trigger)
                .serial(serial)
                .states(states).build();
        kafkaProducer.produceDeviceState(deviceId, value);
    }

    public void notifyChangeConnection(String deviceId, String serial, boolean online) {
        DeviceStateValue value = DeviceStateValue.builder()
                .connection("websocket")
                .serial(serial)
                .online(online).build();
        kafkaProducer.produceDeviceState(deviceId, value);
    }
}