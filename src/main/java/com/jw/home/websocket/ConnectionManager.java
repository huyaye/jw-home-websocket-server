package com.jw.home.websocket;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class ConnectionManager {
    // 기기가 서버에 정상 등록된 세션 <Serial : (Session, deviceId)>
    private final ConcurrentHashMap<String, SessionInfo> deviceSessions = new ConcurrentHashMap<>();

    public void putSessionInfo(String serial, SessionInfo sessionInfo) {
        deviceSessions.put(serial, sessionInfo);
    }

    public void removeSessionInfo(String serial) {
        deviceSessions.remove(serial);
    }

    public SessionInfo getSessionInfo(String serial) {
        return deviceSessions.get(serial);
    }

    public boolean existSessionInfo(String serial) {
        return deviceSessions.get(serial) != null;
    }

}
