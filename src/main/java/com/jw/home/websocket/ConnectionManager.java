package com.jw.home.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConnectionManager {
    private final ConcurrentHashMap<String, WebSocketSession> deviceSessions = new ConcurrentHashMap<>();   // 기기가 서버에 정상 등록된 세션

    public void addSession(String serial, WebSocketSession session) {
        deviceSessions.put(serial, session);
    }

    public void removeSession(String serial) {
        deviceSessions.remove(serial);
    }

    public WebSocketSession getSession(String serial) {
        return deviceSessions.get(serial);
    }
}
