package one.entropy.karamel.rest;

import io.smallrye.mutiny.Multi;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.eventbus.EventBus;
import one.entropy.karamel.api.KaramelAPI;
import one.entropy.karamel.data.SessionBrokers;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

import static one.entropy.karamel.api.CamelAPI.BROKERS_ADDRESS;

@Path("/broker")
public class BrokerResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrokerResource.class.getCanonicalName());

    @Inject
    KaramelAPI karamelAPI;

    @Inject
    EventBus eventBus;


    @POST
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
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<String> brokers() {
        Collection<String> brokerList = karamelAPI.getBrokers();
        return Multi.createFrom().iterable(brokerList);
    }
}
