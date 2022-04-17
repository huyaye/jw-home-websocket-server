package com.jw.home.rest.controller;

import com.jw.home.exception.WebSocketSessionException;
import com.jw.home.rest.AsyncResponseManager;
import com.jw.home.rest.dto.ControlDeviceReq;
import com.jw.home.service.DeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/devices")
@Slf4j
public class DeviceRestController {
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private AsyncResponseManager asyncResponseManager;

    @PutMapping("/control")
    DeferredResult<ResponseEntity<Map<String, Object>>> controlDevice(@RequestHeader("TRANSACTION_ID") String transactionId, @RequestBody ControlDeviceReq req) {
        log.info("Control device : {}", req);

        DeferredResult<ResponseEntity<Map<String, Object>>> deferredResult = new DeferredResult<>();
        deferredResult.onTimeout(() -> {
            log.warn("device control response timeout.");
            deferredResult.setResult(new ResponseEntity<>(Collections.emptyMap(), HttpStatus.CONFLICT));
        });
        deferredResult.onCompletion(() -> asyncResponseManager.removeResponse(transactionId));
        asyncResponseManager.registerResponse(transactionId, deferredResult);

        try {
            deviceService.controlDevice(req, transactionId);
        } catch (WebSocketSessionException e) {
            log.warn("Failed to control device");
            deferredResult.setResult(new ResponseEntity<>(Collections.emptyMap(), HttpStatus.CONFLICT));
        }

        return deferredResult;
    }
}
