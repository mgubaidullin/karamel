package one.entropy.karamel;

import javax.ws.rs.FormParam;

public class KaramelMessageForm {

    public @FormParam("topic")  String topic;
    public @FormParam("partition")  Long partition;
    public @FormParam("offset")  Long offset;
    public @FormParam("timestamp")  Long timestamp;
    public @FormParam("key")  String key;
    public @FormParam("value")  String value;

    public KaramelMessage getKaramelMessage() {
        return new KaramelMessage(topic, partition, offset, timestamp, key, value);
    }

}
