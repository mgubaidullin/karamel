package one.entropy.karamel.ui;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.vavr.control.Try;
import one.entropy.karamel.api.KafkaAPI;
import one.entropy.karamel.api.StrimziAPI;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.Node;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

@Path("/")
public class KafkaUI {

    @Inject
    KafkaAPI kafkaAPI;

    @Inject
    StrimziAPI strimziAPI;

    @Inject
    Template kafka;

    @GET
    @Consumes(MediaType.TEXT_HTML)
    @Produces(MediaType.TEXT_HTML)
    @Path("kafka")
    public TemplateInstance kafka() {
        String brokers = Try.of(() -> strimziAPI.getBrokers().toCompletableFuture().get().get(0)).getOrElse("");
        Collection<Node> nodes = kafkaAPI.getNodes(brokers);
        return kafka
                .data("nodes", nodes)
                .data("page", "kafka");
    }

    @Inject
    Template zookeeper;

    @GET
    @Consumes(MediaType.TEXT_HTML)
    @Produces(MediaType.TEXT_HTML)
    @Path("zookeeper")
    public TemplateInstance zookeeper() {
        return zookeeper.data("page", "zookeeper");
    }
}
