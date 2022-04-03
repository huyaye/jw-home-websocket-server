package com.jw.home.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class AsyncResponseManager {
    // Key - TransactionId
    private final Map<String, DeferredResult<ResponseEntity<Map<String, Object>>>> responseMap = new HashMap<>();

    public void registerResponse(String transactionId, DeferredResult<ResponseEntity<Map<String, Object>>> deferredResult) {
        responseMap.put(transactionId, deferredResult);
    }

    public void removeResponse(String transactionId) {
        responseMap.remove(transactionId);
        log.info("transaction is removed : {}", transactionId);
    }

    public void sendResult(String transactionId, Map<String, Object> data) {
        DeferredResult<ResponseEntity<Map<String, Object>>> deferredResult = responseMap.get(transactionId);
        if (deferredResult == null) {
            log.warn("Not found transactionId : {}", transactionId);
            return;
        }
        deferredResult.setResult(new ResponseEntity<>(data, HttpStatus.OK));
    }
}
