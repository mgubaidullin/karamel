package one.entropy.karamel.ui;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.vavr.control.Try;
import one.entropy.karamel.api.KafkaAPI;
import one.entropy.karamel.api.KaramelAPI;
import org.apache.kafka.common.Node;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Path("/")
public class KafkaUI {

    @Inject
    KafkaAPI kafkaAPI;

    @Inject
    KaramelAPI karamelAPI;

    @Inject
    Template kafka;

    @GET
    @Consumes(MediaType.TEXT_HTML)
    @Produces(MediaType.TEXT_HTML)
    @Path("kafka")
    public TemplateInstance kafka() {
        CompletionStage<List<String>> list = karamelAPI.getBrokers();
        CompletionStage<Collection<Node>> nodeList = list.thenCompose(brokers -> kafkaAPI.getNodes(brokers.get(0)));
        Collection<Node> nodes = Try.of(() -> nodeList.toCompletableFuture().get()).getOrElse(List.of());
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
