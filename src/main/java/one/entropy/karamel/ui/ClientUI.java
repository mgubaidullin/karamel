package one.entropy.karamel.ui;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import one.entropy.karamel.api.KafkaAPI;
import one.entropy.karamel.api.KaramelSocket;
import one.entropy.karamel.data.JsonUtil;
import one.entropy.karamel.data.KEventIn;
import one.entropy.karamel.data.KEventOut;
import one.entropy.karamel.route.KaramelConsumer;
import one.entropy.karamel.route.KaramelProducer;
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
import java.util.stream.Collectors;

@Path("/")
public class ClientUI {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientUI.class.getCanonicalName());

    @Inject
    KafkaAPI kafkaAPI;

    @Inject
    Template client;

    @Inject
    KaramelProducer karamelProducer;

    @Inject
    KaramelConsumer karamelConsumer;

    @GET
    @Consumes(MediaType.TEXT_HTML)
    @Produces(MediaType.TEXT_HTML)
    @Path("client")
    public TemplateInstance client() {
        return client
                .data("topics", List.of())
                .data("brokerListHeader", "Brokers")
                .data("kmessages", karamelConsumer.getSortedEvents())
                .data("kmessage", new KEventIn())
                .data("page", "client")
                .data("view", false);
    }

    @GET
    @Consumes(MediaType.TEXT_HTML)
    @Produces(MediaType.TEXT_HTML)
    @Path("client/{brokers}")
    public TemplateInstance client(@PathParam("brokers") String brokers, @Context HttpServletRequest request) {
        String sessionId = request.getSession(true).getId();
        karamelProducer.create(brokers, sessionId);
        karamelConsumer.create(brokers, sessionId);
        Collection<String> topics = kafkaAPI.getTopics(brokers).stream().map(td -> td.name()).collect(Collectors.toList());
        return client
                .data("topics", topics)
                .data("brokerListHeader", brokers)
                .data("brokers", brokers)
                .data("kmessages", karamelConsumer.getSortedEvents())
                .data("kmessage", new KEventIn())
                .data("page", "client")
                .data("view", false);
    }

//    @ConsumeEvent(value = "kmessage")
//    public void consume(Message<KaramelMessage> message) {
//        kmessages.add(message.body());
//        karamelSocket.broadcast("refresh");
//    }
//
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Path("consumer")
//    public List<KaramelMessage> listMessagesJson(@QueryParam("filter") String filter) {
//        return find(filter);
//    }
//
//
//    @GET
//    @Produces(MediaType.TEXT_HTML)
//    @Path("/message/{id}")
//    public TemplateInstance openMessageForm(@PathParam("id") String id) {
//        KaramelMessage kmessage = kmessages.stream().filter(km -> Objects.equals(km.getId(), id)).findFirst().get();
//        return message.data("kmessage", kmessage).data("view", true);
//    }
//
//    @GET
//    @Produces(MediaType.TEXT_HTML)
//    @Path("/producer")
//    public TemplateInstance openProducerForm() {
//        return message.data("kmessage", new KaramelMessage())
//                .data("consumer", false)
//                .data("producer", true)
//                .data("view", false);
//    }
//
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/message")
    public Response produce(@MultipartForm String json) {
        LOGGER.info("PUBLISH: {}", json);
        KEventOut kevent = JsonUtil.fromJson(json, KEventOut.class);
        karamelProducer.publish(kevent);
        return Response.status(200).build();//.location(URI.create("/client")).build();
    }
//
//    @POST
//    @Path("/restart")
//    public Response restart() {
//        context.createProducerTemplate().sendBody("controlbus:route?routeId="+CONSUMER_ROUTE_ID+"&action=restart", null);
//        return Response.status(301).location(URI.create("/consumer")).build();
//    }
}
