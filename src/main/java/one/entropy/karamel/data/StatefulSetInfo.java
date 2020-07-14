package one.entropy.karamel.data;

import io.quarkus.qute.TemplateData;

@TemplateData
public class StatefulSetInfo {

    public String kind;
    public String uid;
    public String name;
    public int replicas;
    public int readyReplicas;

    public StatefulSetInfo() {
    }

    public StatefulSetInfo(String kind, String uid, String name, int replicas, int readyReplicas) {
        this.kind = kind;
        this.uid = uid;
        this.name = name;
        this.replicas = replicas;
        this.readyReplicas = readyReplicas;
    }
}
