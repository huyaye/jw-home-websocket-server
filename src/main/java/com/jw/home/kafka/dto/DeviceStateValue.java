package com.jw.home.kafka.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jw.home.domain.TriggerType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceStateValue {
    String connection;

    String serial;

    Boolean online;

    TriggerType trigger;

    Map<String, Object> states;
}
