package com.jw.home.rest;

import com.jw.home.common.HttpUtil;
import com.jw.home.rest.dto.APIServerResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@Slf4j
public class APIServerCaller {
    private final RestTemplate restTemplate;

    @Value(value = "${api-server.url}")
    private String apiServerUrl;

    public APIServerCaller() {
        this.restTemplate = new RestTemplate(HttpUtil.getHttpRequestFactory());
    }

    public String registerDevice(Map<String, Object> payload) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.add("Authorization", "Bearer " + clientAccessToken);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        String url = apiServerUrl + "/api/v1/devices";
        try {
            ResponseEntity<APIServerResponseDto> response = restTemplate.postForEntity(url, request, APIServerResponseDto.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                return null;
            }
            return (String) response.getBody().getResultData().get("id");
        } catch (Exception e) {
            log.warn("Failed to call API server (registerDevice) : {}", e.getMessage());
        }
        return null;
    }

    public String getDeviceId(String serial) {
        String url = apiServerUrl + "/api/v1/admin/devices/id?connection=websocket&serial=" + serial;
        try {
            ResponseEntity<APIServerResponseDto> response = restTemplate.getForEntity(url, APIServerResponseDto.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                return null;
            }
            return (String) response.getBody().getResultData().get("id");
        } catch (Exception e) {
            log.info("Failed to call API server (getDeviceId) : {}", e.getMessage());
        }
        return null;
    }
}
