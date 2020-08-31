package one.entropy.karamel.rest;

import io.smallrye.mutiny.Multi;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.eventbus.EventBus;
import one.entropy.karamel.data.JsonUtil;
import one.entropy.karamel.data.KEventOut;
import one.entropy.karamel.data.StartEvent;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static one.entropy.karamel.service.CamelService.*;

@Path("/api/message")
public class MessageResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageResource.class.getCanonicalName());
    private static final String PREFIX = "MESSAGE_";

    @Inject
    EventBus eventBus;

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @Path("/{sessionId}")
    public Multi<JsonObject> eventSourcing(@PathParam("sessionId") String sessionId) {
        LOGGER.info("Start sourcing for session: {}", sessionId);
        return eventBus.<JsonObject>consumer(PREFIX + sessionId).toMulti().map(event -> event.body());
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response produce(@MultipartForm String json) {
        LOGGER.info("Publishing: {}", json);
        KEventOut kevent = JsonUtil.fromJson(json, KEventOut.class);
        eventBus.publish(MESSAGE_ADDRESS, kevent);
        return Response.ok().build(); // TODO: check result before response
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/start")
    public Response start(@MultipartForm String request) {
        LOGGER.info("Start consuming: {}", request);
        JsonObject json = new JsonObject(request);
        String sessionId = json.getString("sessionId");
        String broker = json.getString("broker");
        String filter = json.getString("filter");
        eventBus.publish(BROKERS_ADDRESS_START, new StartEvent(PREFIX + sessionId, broker, filter));
        eventBus.publish(PREFIX + sessionId, new JsonObject().put("type", "test"));
        return Response.ok().build(); // TODO: check result before response
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/stop/{sessionId}")
    public Response stop(@PathParam("sessionId") String sessionId) {
        LOGGER.info("Stopping messages for session: {}", sessionId);
        eventBus.publish(BROKERS_ADDRESS_STOP, PREFIX + sessionId);
        return Response.ok().build(); // TODO: check result before response
    }
}
