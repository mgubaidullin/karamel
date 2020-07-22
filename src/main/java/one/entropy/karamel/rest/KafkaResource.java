package one.entropy.karamel.rest;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.eventbus.EventBus;
import one.entropy.karamel.data.NodeInfo;
import one.entropy.karamel.service.KafkaService;
import one.entropy.karamel.service.KubernetesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.List;

@Path("/api")
public class KafkaResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaResource.class.getCanonicalName());

    @Inject
    BrokerResource brokerResource;

    @Inject
    KafkaService kafkaService;

    @GET
    @Path("/node")
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<NodeInfo> nodes() {
        return brokerResource.brokers()
                .map(s -> kafkaService.getNodes(s))
                .flatMap(i -> Multi.createFrom().iterable(i));
    }
}
