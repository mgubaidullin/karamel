package one.entropy.karamel.data;

public class NodeInfo {

    public String id;
    public String host;
    public int port;
    public String rack;

    public NodeInfo() {
    }

    public NodeInfo(String id, String host, int port, String rack) {
        this.id = id;
        this.host = host;
        this.port = port;
        this.rack = rack;
    }
}
