package one.entropy.karamel.data;

import io.vavr.control.Try;
import lombok.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Builder
public class KEventIn {

    private String sessionId;
    private String topic;
    private Long partition;
    private Long offset;
    private Instant timestamp;
    private String key;
    private String value;

    public String getId(){
        return topic + "-" + offset;
    }

    public String getTimestampString(){
//        System.out.println(DateTimeFormatter.ISO_INSTANT.format(timestamp));
//        System.out.println(DateTimeFormatter.ISO_DATE_TIME.format(timestamp.atZone(ZoneId.systemDefault())));
        return DateTimeFormatter.ISO_INSTANT.format(timestamp.atZone(ZoneId.systemDefault()));
    }

    public boolean showLink(){
        return Try.of(() -> value.length() > 100).getOrElse(false);
    }
}
