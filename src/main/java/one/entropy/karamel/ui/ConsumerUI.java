package one.entropy.karamel.ui;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.vertx.ConsumeEvent;
import io.vertx.reactivex.core.eventbus.Message;
import one.entropy.karamel.api.KaramelSocket;
import one.entropy.karamel.data.KaramelMessage;
import org.apache.camel.CamelContext;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static one.entropy.karamel.route.KaramelRoute.CONSUMER_ROUTE_ID;

@Path("/")
public class ConsumerUI {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerUI.class.getCanonicalName());

    @Inject
    KaramelSocket karamelSocket;

    @Inject
    Template error;

    @Inject
    Template message;

    @Inject
    Template producer;

    @Inject
    Template consumer;

    @Inject
    CamelContext context;

    private static final List<KaramelMessage> kmessages = new ArrayList();

    @GET
    @Consumes(MediaType.TEXT_HTML)
    @Produces(MediaType.TEXT_HTML)
    @Path("consumer")
    public TemplateInstance consumer(@QueryParam("filter") String filter) {
        return consumer.data("kmessages", find(filter))
                .data("filter", filter)
                .data("page", "consumer")
                .data("filtered", filter != null && !filter.isEmpty());
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
    private List<KaramelMessage> find(String filter) {
        return filter != null && !filter.isEmpty()
                ? kmessages.stream().filter(km -> km.getTopic().contains(filter)).sorted(Comparator.comparing(KaramelMessage::getTimestamp).reversed()).collect(Collectors.toList())
                : kmessages.stream().sorted(Comparator.comparing(KaramelMessage::getTimestamp).reversed()).collect(Collectors.toList());
    }
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
//    @POST
//    @Consumes(MediaType.MULTIPART_FORM_DATA)
//    @Path("/publish")
//    public Response publish(@MultipartForm KaramelMessageForm form) {
//        KaramelMessage km = form.getKaramelMessage();
//        context.createProducerTemplate().sendBody("direct:message", km);
//        return Response.status(301).location(URI.create("/consumer")).build();
//    }
//
//    @POST
//    @Path("/restart")
//    public Response restart() {
//        context.createProducerTemplate().sendBody("controlbus:route?routeId="+CONSUMER_ROUTE_ID+"&action=restart", null);
//        return Response.status(301).location(URI.create("/consumer")).build();
//    }
}
