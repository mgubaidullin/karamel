package one.entropy.karamel.api;

import io.vavr.control.Try;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
@Named("kafkaAPI")
public class KafkaAPI {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaAPI.class.getCanonicalName());

    public CompletionStage<Collection<TopicDescription>> getTopics(String brokers) {
        return CompletableFuture.supplyAsync(() -> getKafkaTopics(brokers)).completeOnTimeout(List.of(), 5, TimeUnit.SECONDS);
    }

    private Collection<TopicDescription> getKafkaTopics(String brokers) {
        return Try.of(() -> {
            Map<String, Object> conf = new HashMap<>();
            conf.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
            conf.put(AdminClientConfig.CLIENT_ID_CONFIG, "karamel");
            AdminClient adminClient = KafkaAdminClient.create(conf);

            ListTopicsOptions options = new ListTopicsOptions();
            options.listInternal(true);

            Collection<String> topicNames =  adminClient.listTopics(options).names().get();
            Collection<TopicDescription> list =  adminClient.describeTopics(topicNames).all().get().values();
            adminClient.close();
            return list;
        }).onFailure((throwable -> LOGGER.error("", throwable))).getOrElse(Collections.EMPTY_LIST);
    }

    public CompletionStage<Collection<Node>> getNodes(String brokers) {
        return CompletableFuture.supplyAsync(() -> getKafkaNodes(brokers)).completeOnTimeout(List.of(), 5, TimeUnit.SECONDS);
    }
    private Collection<Node> getKafkaNodes(String brokers) {
        return Try.of(() -> {
            Map<String, Object> conf = new HashMap<>();
            conf.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
            conf.put(AdminClientConfig.CLIENT_ID_CONFIG, "karamel");
            AdminClient adminClient = KafkaAdminClient.create(conf);


            DescribeClusterOptions options = new DescribeClusterOptions();
            options.includeAuthorizedOperations(true);

            Collection<Node> nodes =  adminClient.describeCluster(options).nodes().get();
            adminClient.close();
            return nodes;
        }).onFailure((throwable -> LOGGER.error("", throwable))).getOrElse(Collections.EMPTY_LIST);
    }
}
