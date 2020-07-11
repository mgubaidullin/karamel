package one.entropy.karamel.data;

import io.quarkus.qute.TemplateData;
import io.quarkus.runtime.annotations.RegisterForReflection;

@TemplateData
@RegisterForReflection
public class KEventOut {

    public String topic;
    public String key;
    public String value;

    public KEventOut() {
    }

    public KEventOut(String topic, String key, String value) {
        this.topic = topic;
        this.key = key;
        this.value = value;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
