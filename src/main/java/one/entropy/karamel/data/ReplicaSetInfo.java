package one.entropy.karamel.data;

public class ReplicaSetInfo {

    public String kind;
    public String uid;
    public String name;
    public int replicas;
    public int readyReplicas;

    public ReplicaSetInfo() {
    }

    public ReplicaSetInfo(String kind, String uid, String name, int replicas, int readyReplicas) {
        this.kind = kind;
        this.uid = uid;
        this.name = name;
        this.replicas = replicas;
        this.readyReplicas = readyReplicas;
    }
}
