package one.entropy.karamel.ui;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import one.entropy.karamel.api.KafkaAPI;
import one.entropy.karamel.api.KaramelAPI;
import one.entropy.karamel.data.NodeInfo;
import one.entropy.karamel.data.PodInfo;
import org.apache.kafka.common.Node;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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


        boolean isKubernetes = karamelAPI.isKubernetes();
        if (isKubernetes) {
            Collection<StatefulSet> statefulSets = karamelAPI.getKafkaStatefulSets();
            Collection<PodInfo> pods = karamelAPI.getKafkaPods();
            Collection<Service> bootstrapServices = karamelAPI.getBootstrapServices();
            Collection<Service> brokerServices = karamelAPI.getBrokerServices();
            kafka
                    .data("statefulSets", statefulSets)
                    .data("bootstrapServices", bootstrapServices)
                    .data("brokerServices", brokerServices)
                    .data("pods", pods);
        }
        List<String> list = karamelAPI.getBrokers();
        Collection<NodeInfo> nodeList = kafkaAPI.getNodes(list.get(0));
        return kafka
                .data("nodes", nodeList)
                .data("page", "kafka");
    }

    @Inject
    Template zookeeper;

    @GET
    @Consumes(MediaType.TEXT_HTML)
    @Produces(MediaType.TEXT_HTML)
    @Path("zookeeper")
    public TemplateInstance zookeeper() {
        Collection<StatefulSet> statefulSets = karamelAPI.getZookeeperStatefulSets();
        Collection<PodInfo> pods = karamelAPI.getZookeeperPods();
        return zookeeper
                .data("statefulSets", statefulSets)
                .data("pods", pods)
                .data("page", "zookeeper");
    }
}
