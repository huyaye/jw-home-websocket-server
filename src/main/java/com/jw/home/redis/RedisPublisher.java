package com.jw.home.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RedisPublisher {
    @Value(value = "${spring.redis.pubsub.device.control-response-channel}")
    private String controlResponseChannel;

    @Lazy
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void publishDeviceControlResult(Map<String, Object> result) {
        redisTemplate.convertAndSend(controlResponseChannel, result);
    }
}
