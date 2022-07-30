package com.jw.home.client;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ApiServerClientErrorDecoder implements ErrorDecoder {
    @SneakyThrows
    @Override
    public Exception decode(String methodKey, Response response) {
        log.warn("Failed to call API server : {}, {}", response.status(), response.reason());
        return new Exception(response.reason());
    }
}
