package com.jw.home.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Component
@Slf4j
public class APIServerCaller {
    private final ApiServerClient apiServerClient;

    public APIServerCaller(ApiServerClient apiServerClient) {
        this.apiServerClient = apiServerClient;
    }

    public String registerDevice(Map<String, Object> payload) {
        try {
            ResponseEntity<APIServerResponseDto> response = apiServerClient.registerDevice(payload);
            return (String) response.getBody().getResultData().get("id");
        } catch (Exception e) {
            log.warn("Failed to registerDevice : {}", e.getMessage());
            return null;
        }
    }

    public String getDeviceId(String serial) {
        try {
            ResponseEntity<APIServerResponseDto> response = apiServerClient.getDeviceId("websocket", serial);
            return (String) response.getBody().getResultData().get("id");
        } catch (Exception e) {
            log.info("Failed to getDeviceId : {}", e.getMessage());
            return null;
        }
    }

    @FeignClient(name = "api-server", configuration = ApiServerClientErrorDecoder.class)
    public interface ApiServerClient {
        @PostMapping(value = "/api/v1/devices", headers = "Content-Type=application/json")
        ResponseEntity<APIServerResponseDto> registerDevice(@RequestBody Map<String, Object> payload);

        @GetMapping(value = "/api/v1/admin/devices/id")
        ResponseEntity<APIServerResponseDto> getDeviceId(
                @RequestParam(name="connection") String connection,
                @RequestParam(name = "serial") String serial);
    }
}
