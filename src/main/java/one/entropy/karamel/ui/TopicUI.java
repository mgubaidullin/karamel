package one.entropy.karamel.ui;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.vavr.control.Try;
import org.apache.kafka.clients.admin.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;

@Path("/")
public class TopicUI {
    private static final Logger LOGGER = LoggerFactory.getLogger(TopicUI.class.getCanonicalName());

    @Inject
    Template topics;

    @GET
    @Consumes(MediaType.TEXT_HTML)
    @Produces(MediaType.TEXT_HTML)
    @Path("topics/{brokers}")
    public TemplateInstance operators(@PathParam("brokers") String brokers) {
        Collection<TopicDescription> topicList = getTopics(brokers);
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

    public Collection<TopicDescription> getTopics(String brokers) {
        return Try.of(() -> {
            Map<String, Object> conf = new HashMap<>();
            conf.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
            conf.put(AdminClientConfig.CLIENT_ID_CONFIG, "karamel");
            AdminClient adminClient = KafkaAdminClient.create(conf);

            ListTopicsOptions options = new ListTopicsOptions();
            options.listInternal(true);

            Collection<String> topicNames =  adminClient.listTopics(options).names().get();
            Collection<TopicDescription> list =  adminClient.describeTopics(topicNames).all().get().values();
            adminClient.close();
            return list;
        }).onFailure((throwable -> LOGGER.error("", throwable))).getOrElse(Collections.EMPTY_LIST);
    }
}

