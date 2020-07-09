package one.entropy.karamel.kafka;

import io.vavr.control.Try;
import io.vertx.mutiny.core.eventbus.EventBus;
import one.entropy.karamel.api.KaramelSocket;
import one.entropy.karamel.data.KEventIn;
import one.entropy.karamel.ui.KaramelSession;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.endpoint.EndpointRouteBuilder;
import org.apache.camel.component.kafka.KafkaConstants;
import org.apache.camel.quarkus.core.FastCamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@SessionScoped
public class KaramelConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(KaramelConsumer.class.getCanonicalName());

    private String consumerBrokers;
    private String sessionId;
    private final List<KEventIn> events = new ArrayList<>();
    @Inject
    CamelContext context;
    @Inject
    EventBus bus;

    @Inject
    KaramelSession session;

    public void create(String sessionId) {
        this.sessionId = sessionId;
        if (context.getRoute(sessionId) != null && Objects.equals(session.getBrokers(), consumerBrokers)) {
            LOGGER.info("Reuse consumer for : {}", consumerBrokers);
        } else if (context.getRoute(sessionId) != null && !Objects.equals(session.getBrokers(), consumerBrokers)) {
            close();
            initialize(session.getBrokers());
        } else {
            initialize(session.getBrokers());
        }
    }

    public void reconnect() throws Exception {
        ((FastCamelContext)context).stopRoute(sessionId);
        ((FastCamelContext)context).startRoute(sessionId);
    }

    private void initialize(String brokers) {
        LOGGER.info("Create consumer route for : {}", brokers);
        this.consumerBrokers = brokers;
        this.events.clear();
        Try.run(() -> {
            context.addRoutes(createRouteBuilder());
        }).onFailure(throwable -> LOGGER.error("", throwable));
    }

    private void process(Exchange exchange) {
        Message message = exchange.getIn();
        KEventIn kevent = KEventIn.builder()
                .sessionId(sessionId)
                .topic(message.getHeader(KafkaConstants.TOPIC, String.class))
                .partition(message.getHeader(KafkaConstants.PARTITION, Long.class))
                .offset(message.getHeader(KafkaConstants.OFFSET, Long.class))
                .timestamp(Instant.ofEpochMilli(exchange.getIn().getHeader(KafkaConstants.TIMESTAMP, Long.class)))
                .key(message.getHeader(KafkaConstants.KEY, String.class))
                .value(message.getBody(String.class))
                .build();
        events.add(kevent);
        bus.publish(KaramelSocket.ADDRESS, kevent);
    }

    private RouteBuilder createRouteBuilder() {
        return new EndpointRouteBuilder() {
            @Override
            public void configure() throws Exception {
                EndpointConsumerBuilder endpoint = kafka(".*").groupId(sessionId).brokers(KaramelConsumer.this.consumerBrokers).topicIsPattern(true).autoOffsetReset("earliest");
                from(endpoint).routeId(sessionId).process(KaramelConsumer.this::process);
            }
        };
    }

    @PreDestroy
    void close() {
        Try.run(() -> {
            boolean result = context.removeRoute(sessionId);
            LOGGER.info("Consumer route {} removed result = {}", sessionId, result);
        }).onFailure(throwable -> LOGGER.error("", throwable));
    }

    public List<KEventIn> getSortedEvents() {
        return events.stream().sorted(Comparator.comparing(KEventIn::getTimestamp).reversed()).collect(Collectors.toList());
    }

    public KEventIn findEvent(String topic, Long partition, Long offset) {
        return events.stream().filter(e ->
                Objects.equals(e.getTopic(), topic) && Objects.equals(e.getPartition(), partition) && Objects.equals(e.getOffset(), offset)
        ).findFirst().get();
    }
}
