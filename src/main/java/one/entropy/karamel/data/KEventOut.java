package one.entropy.karamel.data;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Builder
public class KEventOut {

    private String topic;
    private String key;
    private String value;

}
