package one.entropy.karamel.rest;

import io.smallrye.mutiny.Multi;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.eventbus.EventBus;
import one.entropy.karamel.api.KafkaAPI;
import one.entropy.karamel.api.KaramelAPI;
import one.entropy.karamel.data.NodeInfo;
import one.entropy.karamel.data.SessionBrokers;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.List;

import static one.entropy.karamel.api.CamelAPI.BROKERS_ADDRESS;

@Path("/api")
public class KafkaResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaResource.class.getCanonicalName());

    @Inject
    KaramelAPI karamelAPI;

    @Inject
    KafkaAPI kafkaAPI;

    @Inject
    EventBus eventBus;


    @POST
    @Path("/broker")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setSessionBrokers(@MultipartForm String json) {
        LOGGER.info("Set session brokers: {}", json);
        String brokers = new JsonObject(json).getString("brokers");
        String sessionId = new JsonObject(json).getString("sessionId");
        eventBus.publish(BROKERS_ADDRESS, new SessionBrokers(sessionId, brokers));
        return Response.ok().build(); // TODO: check result before response
    }

    @GET
    @Path("/broker")
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<String> brokers() {
        Collection<String> brokerList = karamelAPI.getBrokers();
        return Multi.createFrom().iterable(brokerList);
    }

    @GET
    @Path("/node")
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<NodeInfo> nodes() {
        List<String> brokerList = karamelAPI.getBrokers();
        Collection<NodeInfo> nodeList = kafkaAPI.getNodes(brokerList.get(0));
        return Multi.createFrom().iterable(nodeList);
    }
}
