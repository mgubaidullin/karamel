package one.entropy.karamel.rest;

import io.smallrye.mutiny.Multi;
import one.entropy.karamel.service.KafkaService;
import one.entropy.karamel.data.TopicInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

@Path("/api")
public class TopicResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(TopicResource.class.getCanonicalName());

    @Inject
    KafkaService kafkaService;

    @GET
    @Path("/topic")
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<TopicInfo> getTopics(@QueryParam("brokers") String brokers) {
        LOGGER.info("Get topics for brokers: {}", brokers);
        Collection<TopicInfo> topicList = kafkaService.getTopics(brokers, true);
        return Multi.createFrom().iterable(topicList);
    }
}
