package com.jw.home.event;

import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

import java.util.Map;

@Getter
@ToString
public class ControlResultEvent extends ApplicationEvent {
    private final String transactionId;
    private final String deviceSerial;
    private final Map<String, Object> data;

    public ControlResultEvent(Object source, String transactionId, String deviceSerial, Map<String, Object> data) {
        super(source);
        this.transactionId = transactionId;
        this.deviceSerial = deviceSerial;
        this.data = data;
    }
}
