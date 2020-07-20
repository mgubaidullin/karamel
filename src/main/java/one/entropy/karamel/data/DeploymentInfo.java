package one.entropy.karamel.data;

public class DeploymentInfo {

    public String kind;
    public String uid;
    public String name;
    public int replicas;
    public int readyReplicas;

    public DeploymentInfo() {
    }

    public DeploymentInfo(String kind, String uid, String name, int replicas, int readyReplicas) {
        this.kind = kind;
        this.uid = uid;
        this.name = name;
        this.replicas = replicas;
        this.readyReplicas = readyReplicas;
    }
}
