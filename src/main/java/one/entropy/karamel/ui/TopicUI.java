package one.entropy.karamel.ui;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.vavr.control.Try;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.KafkaFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Path("/")
public class TopicUI {
    private static final Logger LOGGER = LoggerFactory.getLogger(TopicUI.class.getCanonicalName());

    @Inject
    Template topics;

    @GET
    @Consumes(MediaType.TEXT_HTML)
    @Produces(MediaType.TEXT_HTML)
    @Path("topics/{brokers}")
    public TemplateInstance operators(@PathParam("brokers") String brokers) {
        Collection<TopicDescription> topicList = getTopics(brokers);
        return topics
                .data("topicList", topicList)
                .data("brokerListHeader", brokers)
                .data("brokers", brokers)
                .data("page", "topics");
    }

    @GET
    @Consumes(MediaType.TEXT_HTML)
    @Produces(MediaType.TEXT_HTML)
    @Path("topics")
    public TemplateInstance operators() {
        return topics
                .data("topicList", List.of())
                .data("brokerListHeader", "Brokers")
                .data("brokers", "")
                .data("page", "topics");
    }


    public static boolean checkConnection(String brokers) {
        return Try.of(() -> {
            Map<String, Object> conf = new HashMap<>();
            conf.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
            AdminClient adminClient = KafkaAdminClient.create(conf);
            ListTopicsResult ltr = adminClient.listTopics();
            KafkaFuture<Set<String>> names = ltr.names();
            names.get();
            adminClient.close();
            return true;
        }).onFailure((throwable -> LOGGER.error("", throwable))).getOrElse(false);
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Map<String, Object> conf = new HashMap<>();
        conf.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.64.43:32002");
        conf.put(AdminClientConfig.CLIENT_ID_CONFIG, "karamel");

        ListTopicsOptions options = new ListTopicsOptions();
        options.listInternal(true);

        AdminClient adminClient = KafkaAdminClient.create(conf);
        Collection<String> topicNames =  adminClient.listTopics(options).names().get();

//        System.out.println(adminClient.listTopics(options).names().get());
//        System.out.println(adminClient.listConsumerGroups().all().get());
//        System.out.println(adminClient.describeCluster().clusterId().get());
//        System.out.println(adminClient.describeCluster().controller().get());
//        System.out.println(adminClient.describeCluster().nodes().get());
        System.out.println(adminClient.describeTopics(topicNames).all().get());
        Collection<TopicListing> list =  adminClient.listTopics().listings().get();
        list.forEach(topicListing -> System.out.println(topicListing));
        adminClient.close();
    }

    public Collection<TopicDescription> getTopics(String brokers) {
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
}

