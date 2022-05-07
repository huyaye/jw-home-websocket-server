package com.jw.home.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jw.home.redis.dto.ControlReqMsg;
import com.jw.home.service.DeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisDeviceControlSubscriber implements MessageListener {
    private final DeviceService deviceService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onMessage(Message message, byte[] bytes) {
        log.info("subscribe message : {}", message.toString());
        try {
            ControlReqMsg controlReqMsg = objectMapper.readValue(message.getBody(), ControlReqMsg.class);
            deviceService.controlDevice(controlReqMsg);
        } catch (IOException e) {
            log.warn("Exception during handle request control", e);
        }
    }
}
