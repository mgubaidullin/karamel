package one.entropy.karamel.ui;

import javax.enterprise.context.SessionScoped;

@SessionScoped
public class KaramelSession {

    private String brokers;

    public String getBrokers() {
        return brokers;
    }

    public void setBrokers(String brokers) {
        this.brokers = brokers;
    }
}
