package com.jw.home.rest.dto;

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
public class ControlDeviceReq {
    @NotEmpty
    String serial;
    @NotEmpty
    String command;
    @NotNull
    Map<String, Object> params;
}
