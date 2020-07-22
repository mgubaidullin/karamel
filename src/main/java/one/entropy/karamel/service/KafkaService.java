package one.entropy.karamel.service;

import io.vavr.control.Try;
import one.entropy.karamel.data.NodeInfo;
import one.entropy.karamel.data.TopicInfo;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class KafkaService {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaService.class.getCanonicalName());

    public Collection<TopicInfo> getTopics(String brokers, boolean listInternal) {
        return Try.of(() -> {
            Map<String, Object> conf = new HashMap<>();
            conf.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
            conf.put(AdminClientConfig.CLIENT_ID_CONFIG, "karamel");
            AdminClient adminClient = KafkaAdminClient.create(conf);

            ListTopicsOptions options = new ListTopicsOptions();
            options.listInternal(listInternal);

            Collection<String> topicNames =  adminClient.listTopics(options).names().get();
            Collection<TopicDescription> list =  adminClient.describeTopics(topicNames).all().get().values();
            adminClient.close();
            return list.stream().map(t -> new TopicInfo(t.name(), t.partitions().size(), t.isInternal())).collect(Collectors.toList());
        }).onFailure((throwable -> LOGGER.error("", throwable))).getOrElse(Collections.EMPTY_LIST);
    }

    public Collection<NodeInfo> getNodes(String brokers) {
        return Try.of(() -> {
            Map<String, Object> conf = new HashMap<>();
            conf.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
            conf.put(AdminClientConfig.CLIENT_ID_CONFIG, "karamel");
            AdminClient adminClient = KafkaAdminClient.create(conf);


            DescribeClusterOptions options = new DescribeClusterOptions();
            options.includeAuthorizedOperations(true);

            Collection<Node> nodes = adminClient.describeCluster(options).nodes().get();
            adminClient.close();
            return nodes.stream().map(n -> new NodeInfo(n.idString(), n.host(), n.port(), n.rack())).collect(Collectors.toList());
        }).onFailure((throwable -> LOGGER.error("", throwable))).getOrElse(Collections.EMPTY_LIST);
    }
}
