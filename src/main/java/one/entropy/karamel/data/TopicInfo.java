package one.entropy.karamel.data;

import io.quarkus.qute.TemplateData;

@TemplateData
public class TopicInfo {

    public String name;
    public int partitions;
    public boolean internal;

    public TopicInfo() {
    }

    public TopicInfo(String name, int partitions, boolean internal) {
        this.name = name;
        this.partitions = partitions;
        this.internal = internal;
    }
}
