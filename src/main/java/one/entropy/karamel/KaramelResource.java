package one.entropy.karamel;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.vertx.ConsumeEvent;
import io.vertx.reactivex.core.eventbus.Message;
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

@Path("/messages")
public class KaramelResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(KaramelResource.class.getCanonicalName());

    @Inject
    KaramelSocket karamelSocket;

    @Inject
    Template error;

    @Inject
    Template message;

    @Inject
    Template messages;

    @Inject
    CamelContext context;

    private static final List<KaramelMessage> kmessages = new ArrayList();

    @GET
    @Consumes(MediaType.TEXT_HTML)
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance listMessages(@QueryParam("filter") String filter) {
        return messages.data("kmessages", find(filter))
                .data("filter", filter)
                .data("filtered", filter != null && !filter.isEmpty());
    }

    @ConsumeEvent(value = "kmessage")
    public void consume(Message<KaramelMessage> message) {
        LOGGER.info(message.body().toString());
        kmessages.add(message.body());
        karamelSocket.broadcast("refresh");
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<KaramelMessage> listMessagesJson(@QueryParam("filter") String filter) {
        return find(filter);
    }

    private List<KaramelMessage> find(String filter) {
        return filter != null && !filter.isEmpty()
                ? kmessages.stream().filter(km -> km.getTopic().contains(filter)).sorted(Comparator.comparingLong(KaramelMessage::getTimestamp).reversed()).collect(Collectors.toList())
                : kmessages.stream().sorted(Comparator.comparingLong(KaramelMessage::getTimestamp).reversed()).collect(Collectors.toList());
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/open/{id}")
    public TemplateInstance openForm(@PathParam("id") String id) {
        KaramelMessage kmessage = kmessages.stream().filter(km -> Objects.equals(km.getId(), id)).findFirst().get();
        return message.data("kmessage", kmessage).data("view", true);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/new")
    public TemplateInstance newForm() {
        return message.data("kmessage", new KaramelMessage()).data("view", false);
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/publish")
    public Response publish(@MultipartForm KaramelMessageForm form) {
        KaramelMessage km = form.getKaramelMessage();
        LOGGER.info(km.toString());
        context.createProducerTemplate().sendBody("direct:message", km);
        return Response.status(301).location(URI.create("/messages/")).build();
    }
}
