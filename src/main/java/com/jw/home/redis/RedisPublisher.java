package com.jw.home.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class RedisPublisher {
    @Value(value = "${spring.redis.pubsub.device.control-response-channel}")
    private String controlResponseChannel;

    private final RedisTemplate<String, Object> redisTemplate;

    public void publishDeviceControlResult(Map<String, Object> result) {
        redisTemplate.convertAndSend(controlResponseChannel, result);
    }
}
