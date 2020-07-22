package one.entropy.karamel.rest;

import io.smallrye.mutiny.Multi;
import one.entropy.karamel.service.KubernetesService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
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
public class BrokerResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrokerResource.class.getCanonicalName());

    @ConfigProperty(name = "karamel.brokers", defaultValue = "")
    List<String> brokers;

    @Inject
    KubernetesService kubernetesService;

    @GET
    @Path("/broker")
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<String> brokers() {
        if (kubernetesService.isKubernetes()) {
            Collection<String> brokerList = kubernetesService.getBrokers();
            return Multi.createFrom().iterable(brokerList);
        } else {
            return Multi.createFrom().iterable(brokers);
        }
    }
}
