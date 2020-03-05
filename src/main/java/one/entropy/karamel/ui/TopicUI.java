package one.entropy.karamel.ui;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import one.entropy.karamel.api.KafkaAPI;
import org.apache.kafka.clients.admin.TopicDescription;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.List;

@Path("/")
public class TopicUI {

    @Inject
    KafkaAPI kafkaAPI;

    @Inject
    Template topics;

    @GET
    @Consumes(MediaType.TEXT_HTML)
    @Produces(MediaType.TEXT_HTML)
    @Path("topics/{brokers}")
    public TemplateInstance operators(@PathParam("brokers") String brokers) {
        Collection<TopicDescription> topicList = kafkaAPI.getTopics(brokers);
        return topics
                .data("topicList", topicList)
                .data("brokerListHeader", brokers)
                .data("brokers", brokers)
                .data("page", "topics");
    }

    @GET
    @Consumes(MediaType.TEXT_HTML)
    @Produces(MediaType.TEXT_HTML)
    @Path("topics")
    public TemplateInstance operators() {
        return topics
                .data("topicList", List.of())
                .data("brokerListHeader", "Brokers")
                .data("brokers", "")
                .data("page", "topics");
    }


}

