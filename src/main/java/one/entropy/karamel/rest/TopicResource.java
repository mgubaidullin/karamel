package one.entropy.karamel.rest;

import io.smallrye.mutiny.Multi;
import one.entropy.karamel.api.KafkaAPI;
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

@Path("/topic")
public class TopicResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(TopicResource.class.getCanonicalName());

    @Inject
    KafkaAPI kafkaAPI;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<TopicInfo> getTopics(@QueryParam("brokers") String brokers) {
        Collection<TopicInfo> topicList = kafkaAPI.getTopics(brokers, true);
        return Multi.createFrom().iterable(topicList);
    }
}
