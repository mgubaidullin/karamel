package one.entropy.karamel.data;

public class ServiceInfo {

    public String kind;
    public String uid;
    public String name;
    public String type;
    public String namespace;

    public ServiceInfo() {
    }

    public ServiceInfo(String kind, String uid, String name, String type, String namespace) {
        this.kind = kind;
        this.uid = uid;
        this.name = name;
        this.type = type;
        this.namespace = namespace;
    }
}
