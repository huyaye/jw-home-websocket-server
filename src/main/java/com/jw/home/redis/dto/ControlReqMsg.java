package com.jw.home.redis.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ControlReqMsg {
    @NotEmpty
    String transactionId;
    @NotEmpty
    String serial;
    @NotEmpty
    String command;
    @NotNull
    Map<String, Object> params;
}
