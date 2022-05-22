package com.jw.home.kafka;

import com.jw.home.kafka.dto.DeviceStateValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaProducer {
    @Value(value = "${spring.kafka.topics.device-state}")
    private String deviceStateTopicName;

    private final KafkaTemplate<String, DeviceStateValue> kafkaTemplate;

    public void produceDeviceState(String deviceId, DeviceStateValue value) {
        kafkaTemplate.send(deviceStateTopicName, deviceId, value).addCallback(
            new ListenableFutureCallback<>() {
                @Override
                public void onSuccess(SendResult<String, DeviceStateValue> result) {
                    ProducerRecord<String, DeviceStateValue> record = result.getProducerRecord();
                    log.info("Succeed to produce : {}, {}", record.key(), record.value());
                }
                @Override
                public void onFailure(Throwable ex) {
                    log.warn("Failed to produce : ", ex);
                }
            }
        );
    }
}
