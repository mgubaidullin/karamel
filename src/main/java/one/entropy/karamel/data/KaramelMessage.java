package one.entropy.karamel.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@ToString
@Getter
@Setter
public class KaramelMessage {

    private String id;
    private String topic;
    private Long partition;
    private Long offset;
    private Instant timestamp;
    private String key;
    private String value;

    public KaramelMessage() {
    }

    public KaramelMessage(String topic, Long partition, Long offset, Instant timestamp, String key, String value) {
        this.id = topic + "_" + partition + "_" + offset;
        this.topic = topic;
        this.partition = partition;
        this.offset = offset;
        this.timestamp = timestamp;
        this.timestamp = timestamp;
        this.key = key;
        this.value = value;
    }

    public String getTimestampString(){
        System.out.println(DateTimeFormatter.ISO_INSTANT.format(timestamp));
        System.out.println(DateTimeFormatter.ISO_DATE_TIME.format(timestamp.atZone(ZoneId.systemDefault())));
        return DateTimeFormatter.ISO_INSTANT.format(timestamp.atZone(ZoneId.systemDefault()));
    }
}
