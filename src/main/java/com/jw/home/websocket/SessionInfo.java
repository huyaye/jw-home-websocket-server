package com.jw.home.websocket;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.socket.WebSocketSession;

@Getter
@RequiredArgsConstructor
public class SessionInfo {
    final String deviceId;
    final WebSocketSession session;
}