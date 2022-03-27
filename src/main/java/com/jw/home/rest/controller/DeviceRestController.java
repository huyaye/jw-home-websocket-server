package com.jw.home.rest.controller;

import com.jw.home.rest.dto.ControlDeviceReq;
import com.jw.home.rest.dto.ControlDeviceRes;
import com.jw.home.rest.dto.ControlDeviceStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/devices")
@Slf4j
public class DeviceRestController {
    @PutMapping("/control")
    ResponseEntity<ControlDeviceRes> controlDevice(@RequestBody ControlDeviceReq req) {
        log.info("Control device : {}", req);

        ControlDeviceRes response = new ControlDeviceRes();
//        response.setStatus(ControlDeviceStatus.SUCCESS);
//        response.setStates(req.getParams());

        response.setStatus(ControlDeviceStatus.ERROR);
        response.setCause("deviceOffline");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
