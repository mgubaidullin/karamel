package one.entropy.karamel.ui;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.vavr.control.Try;
import io.vertx.core.json.JsonObject;
import one.entropy.karamel.api.KafkaAPI;
import one.entropy.karamel.data.JsonUtil;
import one.entropy.karamel.data.KEventIn;
import one.entropy.karamel.data.KEventOut;
import one.entropy.karamel.kafka.KaramelConsumer;
import one.entropy.karamel.kafka.KaramelProducer;
import org.apache.kafka.clients.admin.TopicDescription;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@Path("/")
public class ClientUI {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientUI.class.getCanonicalName());

    @Inject
    KafkaAPI kafkaAPI;

    @Inject
    Template client;

    @Inject
    Template message;

    @Inject
    KaramelProducer karamelProducer;

    @Inject
    KaramelConsumer karamelConsumer;

    @Inject
    KaramelSession session;

    @GET
    @Consumes(MediaType.TEXT_HTML)
    @Produces(MediaType.TEXT_HTML)
    @Path("client")
    public TemplateInstance client(@Context HttpServletRequest request) {
        if (session.getBrokers() == null) {
            return client
                    .data("topics", List.of())
                    .data("brokerListHeader", "Brokers")
                    .data("kevents", karamelConsumer.getSortedEvents())
                    .data("kevent", new KEventIn())
                    .data("page", "client");
        } else {
            String sessionId = request.getSession(true).getId();
            karamelProducer.create();
            karamelConsumer.create(sessionId);

            CompletionStage<Collection<TopicDescription>> list = kafkaAPI.getTopics(session.getBrokers(), false);
            Collection<String> topics = Try.of(() ->
                    list.toCompletableFuture().get().stream()
                            .filter(td -> !td.name().startsWith("__confluent"))
                            .map(td -> td.name()).collect(Collectors.toList()))
                    .getOrElse(List.of());
            return client
                    .data("topics", topics)
                    .data("brokerListHeader", session.getBrokers())
                    .data("brokers", session.getBrokers())
                    .data("kevents", karamelConsumer.getSortedEvents())
                    .data("kevent", new KEventIn())
                    .data("page", "client");
        }
    }

    @GET
    @Consumes(MediaType.TEXT_HTML)
    @Produces(MediaType.TEXT_HTML)
    @Path("message/{topic}/{partition}/{offset}")
    public TemplateInstance message(@PathParam("topic") String topic, @PathParam("partition") Long partition, @PathParam("offset") Long offset) {
        KEventIn kevent = karamelConsumer.findEvent(topic, partition, offset);
        return message
                .data("kevent", kevent)
                .data("page", "client");
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/message")
    public Response produce(@MultipartForm String json) {
        LOGGER.info("Publishing: {}", json);
        KEventOut kevent = JsonUtil.fromJson(json, KEventOut.class);
        karamelProducer.publish(kevent);
        return Response.status(200).build();//.location(URI.create("/client")).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/brokers")
    public Response brokers(@MultipartForm String json) {
        LOGGER.info("Set {}", json);
        session.setBrokers(new JsonObject(json).getString("brokers"));
        return Response.ok().build();
    }

    @POST
    @Path("/reconnect")
    public Response reconnect(@MultipartForm String json) {
        LOGGER.info("Reconnect {}", json);
        return Try.of(() -> {
            karamelConsumer.reconnect();
            return Response.ok().build();
        }).onFailure(throwable -> LOGGER.error("", throwable))
                .getOrElse(() -> Response.serverError().build());
    }
}
