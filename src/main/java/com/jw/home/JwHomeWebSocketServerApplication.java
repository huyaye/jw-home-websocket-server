package com.jw.home;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class JwHomeWebSocketServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(JwHomeWebSocketServerApplication.class, args);
    }
}
