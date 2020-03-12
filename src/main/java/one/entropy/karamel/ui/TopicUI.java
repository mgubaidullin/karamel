package one.entropy.karamel.ui;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.vavr.control.Try;
import one.entropy.karamel.api.KafkaAPI;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.Node;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletionStage;

@Path("/")
public class TopicUI {

    @Inject
    KafkaAPI kafkaAPI;

    @Inject
    Template topics;

    @Inject
    KaramelSession session;

    @GET
    @Consumes(MediaType.TEXT_HTML)
    @Produces(MediaType.TEXT_HTML)
    @Path("topics")
    public TemplateInstance operators() {
        if (session.getBrokers() != null) {
            CompletionStage<Collection<TopicDescription>> list = kafkaAPI.getTopics(session.getBrokers());
            Collection<TopicDescription> topicList = Try.of(() -> list.toCompletableFuture().get()).getOrElse(List.of());
            return topics
                    .data("topicList", topicList)
                    .data("brokerListHeader", session.getBrokers())
                    .data("brokers", session.getBrokers())
                    .data("page", "topics");
        } else {
            return topics
                    .data("topicList", List.of())
                    .data("brokerListHeader", "Brokers")
                    .data("brokers", "")
                    .data("page", "topics");
        }
    }
}

