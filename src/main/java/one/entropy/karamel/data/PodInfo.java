package one.entropy.karamel.data;

import io.quarkus.qute.TemplateData;

@TemplateData
public class PodInfo {

    public String uid;
    public String name;
    public String phase;
    public  boolean ready;

    public PodInfo() {
    }

    public PodInfo(String uid, String name, String phase, boolean ready) {
        this.uid = uid;
        this.name = name;
        this.phase = phase;
        this.ready = ready;
    }

    public String getClassName(){
         return ready ? "pf-m-success" : "pf-m-danger";
     }

    public String getIcon(){
        return ready ? "fa-check-circle" : "fa-exclamation-circle";
    }

}
