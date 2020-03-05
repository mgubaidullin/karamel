package one.entropy.karamel.route;

import io.vavr.control.Try;
import io.vertx.reactivex.core.eventbus.EventBus;
import one.entropy.karamel.api.KaramelSocket;
import one.entropy.karamel.data.KEventIn;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.endpoint.EndpointRouteBuilder;
import org.apache.camel.component.kafka.KafkaConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static one.entropy.karamel.api.KaramelSocket.ADDRESS;

@SessionScoped
public class KaramelConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(KaramelConsumer.class.getCanonicalName());

    private String brokers;
    private String sessionId;
    private final List<KEventIn> events = new ArrayList<>();
    @Inject
    CamelContext context;
    @Inject
    EventBus bus;

    public void create(String brokers, String sessionId) {
        if (context.getRoute(sessionId) != null && Objects.equals(brokers, this.brokers)) {
            LOGGER.info("Reuse consumer for : {}", brokers);
        } else if (context.getRoute(sessionId) != null && !Objects.equals(brokers, this.brokers)) {
            close();
            initialize(brokers, sessionId);
        } else {
            initialize(brokers, sessionId);
        }
    }

    private void initialize(String brokers, String sessionId) {
        LOGGER.info("Create consumer route for : {}", brokers);
        this.brokers = brokers;
        this.sessionId = sessionId;
        this.events.clear();
        Try.run(() -> {
            context.addRoutes(createRouteBuilder(brokers, sessionId));
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

    private RouteBuilder createRouteBuilder(String brokers, String sessionId) {
        return new EndpointRouteBuilder() {
            @Override
            public void configure() throws Exception {
                EndpointConsumerBuilder endpoint = kafka(".*").groupId(sessionId).brokers(KaramelConsumer.this.brokers).topicIsPattern(true).autoOffsetReset("earliest");
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
}
