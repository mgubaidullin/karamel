package one.entropy.karamel;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class KaramelMessage {

    private String id;
    private String topic;
    private Long partition;
    private Long offset;
    private Long timestamp;
    private String key;
    private String value;

    public KaramelMessage() {
    }

    public KaramelMessage(String topic, Long partition, Long offset, Long timestamp, String key, String value) {
        this.id = topic + "_" + partition + "_" + offset;
        this.topic = topic;
        this.partition = partition;
        this.offset = offset;
        this.timestamp = timestamp;
        this.timestamp = timestamp;
        this.key = key;
        this.value = value;
    }
}
