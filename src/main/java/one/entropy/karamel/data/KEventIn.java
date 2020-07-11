package one.entropy.karamel.data;

import io.quarkus.qute.TemplateData;
import io.vavr.control.Try;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@TemplateData
public class KEventIn {

    public String sessionId;
    public String topic;
    public Long partition;
    public Long offset;
    public Instant timestamp;
    public String key;
    public String value;

    public KEventIn() {
    }

    public KEventIn(String sessionId, String topic, Long partition, Long offset, Instant timestamp, String key, String value) {
        this.sessionId = sessionId;
        this.topic = topic;
        this.partition = partition;
        this.offset = offset;
        this.timestamp = timestamp;
        this.key = key;
        this.value = value;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getId(){
        return topic + "-" + offset;
    }

    public String getTimestampString(){
        return DateTimeFormatter.ISO_INSTANT.format(timestamp.atZone(ZoneId.systemDefault()));
    }

    public boolean showValue(){
        return Try.of(() -> value == null || value.length() < 100).getOrElse(false);
    }

    public boolean showKey(){
        return Try.of(() -> key == null || key.length() < 100).getOrElse(false);
    }
}
