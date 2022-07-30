package com.jw.home.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class APIServerResponseDto {
    private Integer errorCode;

    private String errorMessage;

    private Map<String, Object> resultData;
}