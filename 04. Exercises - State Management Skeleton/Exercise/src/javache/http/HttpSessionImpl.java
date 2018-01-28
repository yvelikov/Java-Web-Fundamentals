package javache.http;

import java.util.HashMap;
import java.util.Map;

public class HttpSessionImpl implements HttpSession {

    private Map<String, Map<String, Object>> allSessions;

    private int maxAge;

    public HttpSessionImpl() {
        this.allSessions = new HashMap<>();
    }

    @Override
    public void setSessionData(String sessionId, Map<String, Object> dataMap) {
        if (!this.allSessions.containsKey(sessionId)) {
            this.allSessions.put(sessionId, dataMap);
        } else {
            for (Map.Entry<String, Object> kvp : dataMap.entrySet()) {
                this.allSessions.get(sessionId).put(kvp.getKey(), kvp.getValue());
            }
        }
    }

    @Override
    public Map<String, Object> getSessionData(String sessionId) {
        return this.allSessions.get(sessionId);
    }

    @Override
    public void removeSession(String sessionId) {
        this.allSessions.remove(sessionId);
    }

    @Override
    public boolean hasSession(String sessionId) {
        return this.allSessions.containsKey(sessionId);
    }
}
