package one.entropy.karamel.data;

public class SessionBrokers {
    private String sessionId;
    private String brokers;

    public SessionBrokers(String sessionId, String brokers) {
        this.sessionId = sessionId;
        this.brokers = brokers;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getBrokers() {
        return brokers;
    }

    public void setBrokers(String brokers) {
        this.brokers = brokers;
    }
}
