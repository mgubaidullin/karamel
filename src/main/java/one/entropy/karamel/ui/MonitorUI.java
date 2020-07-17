package one.entropy.karamel.ui;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import one.entropy.karamel.api.KafkaAPI;
import one.entropy.karamel.api.KaramelAPI;
import one.entropy.karamel.data.TopicInfo;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.List;

@Path("/")
public class MonitorUI {

    @Inject
    KafkaAPI kafkaAPI;

    @Inject
    KaramelAPI karamelAPI;

    @Inject
    Template monitor;


    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("monitor")
    public TemplateInstance operators() {
        Collection<String> brokerList = karamelAPI.getBrokers();
//        if (session.getBrokers() != null) {
//            Collection<TopicInfo> topicList = kafkaAPI.getTopics(session.getBrokers(), true);
//            return monitor
//                    .data("topicList", topicList)
//                    .data("brokerListHeader", session.getBrokers())
//                    .data("brokerList", brokerList)
//                    .data("brokers", session.getBrokers())
//                    .data("page", "monitor");
//        } else {
            return monitor
                    .data("topicList", List.of())
                    .data("brokerListHeader", "Brokers")
                    .data("brokers", "")
                    .data("brokerList", brokerList)
                    .data("page", "monitor");
//        }
    }
}

