package one.entropy.karamel.data;

import lombok.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
@Setter
public class PodInfo {

     String uid;
     String name;
     String phase;
     boolean ready;

     public String getClassName(){
         return ready ? "pf-m-success" : "pf-m-danger";
     }

    public String getIcon(){
        return ready ? "fa-check-circle" : "fa-exclamation-circle";
    }

}
