package com.jw.home.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AppEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    public void publish(ApplicationEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}
