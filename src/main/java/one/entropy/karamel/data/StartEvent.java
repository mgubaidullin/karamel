package one.entropy.karamel.data;

public class StartEvent {
    private String sessionId;
    private String brokers;
    private String filter;

    public StartEvent(String sessionId, String brokers, String filter) {
        this.sessionId = sessionId;
        this.brokers = brokers;
        this.filter = filter;
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

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }
}
