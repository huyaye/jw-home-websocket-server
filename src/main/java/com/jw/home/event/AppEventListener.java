package com.jw.home.event;

import com.jw.home.rest.AsyncResponseManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@Component
@EnableAsync
@RequiredArgsConstructor
@Slf4j
public class AppEventListener {
    private final AsyncResponseManager asyncResponseManager;

    @EventListener
    @Async
    public void handleControlResult(ControlResultEvent event) {
        log.info("handleControlResult >> {}", event);
        asyncResponseManager.sendResult(event.getTransactionId(), event.getData());
    }
}
