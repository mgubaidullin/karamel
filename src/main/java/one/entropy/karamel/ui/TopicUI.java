package one.entropy.karamel.ui;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import one.entropy.karamel.api.KafkaAPI;
import one.entropy.karamel.api.KaramelAPI;
import org.apache.kafka.clients.admin.TopicDescription;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.List;

@Path("/")
public class TopicUI {

    @Inject
    KafkaAPI kafkaAPI;

    @Inject
    KaramelAPI karamelAPI;

    @Inject
    Template topics;

    @Inject
    KaramelSession session;

    @GET
    @Consumes(MediaType.TEXT_HTML)
    @Produces(MediaType.TEXT_HTML)
    @Path("topics")
    public TemplateInstance operators() {
        Collection<String> brokerList = karamelAPI.getBrokers();
        if (session.getBrokers() != null) {
            Collection<TopicDescription> topicList = kafkaAPI.getTopics(session.getBrokers(), true);
            return topics
                    .data("topicList", topicList)
                    .data("brokerListHeader", session.getBrokers())
                    .data("brokerList", brokerList)
                    .data("brokers", session.getBrokers())
                    .data("page", "topics");
        } else {
            return topics
                    .data("topicList", List.of())
                    .data("brokerListHeader", "Brokers")
                    .data("brokers", "")
                    .data("brokerList", brokerList)
                    .data("page", "topics");
        }
    }
}

