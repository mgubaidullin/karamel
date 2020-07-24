package one.entropy.karamel.service;

import io.quarkus.vertx.ConsumeEvent;
import io.vavr.control.Try;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.eventbus.EventBus;
import one.entropy.karamel.data.KEventOut;
import one.entropy.karamel.data.StartEvent;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.EndpointConsumerBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.endpoint.EndpointRouteBuilder;
import org.apache.camel.component.kafka.KafkaConstants;
import org.apache.camel.quarkus.core.FastCamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@ApplicationScoped
public class CamelService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CamelService.class.getCanonicalName());

    public static final String BROKERS_ADDRESS_START = "BROKERS_ADDRESS_START";
    public static final String BROKERS_ADDRESS_STOP = "BROKERS_ADDRESS_STOP";
    public static final String MESSAGE_ADDRESS = "MESSAGE_ADDRESS";

    @Inject
    CamelContext context;

    @Inject
    ProducerTemplate producer;

    @ConsumeEvent(value = BROKERS_ADDRESS_START, blocking = true)
    void startRoutes(StartEvent startEvent) {
        LOGGER.info("Set brokers: {} for session :{}", startEvent.getBrokers(), startEvent.getSessionId());
        String sessionId = startEvent.getSessionId();
        String newBrokers = startEvent.getBrokers();
        String filter = startEvent.getFilter();

        // Stop consumer routes
        Try.run(() -> {
            LOGGER.info("Stoping route for session: {}", sessionId);
            ((FastCamelContext) context).stopRoute(sessionId);
            context.removeRoute(sessionId);
        }).onFailure(throwable -> LOGGER.error("", throwable));

        // Start routes
        Try.run(() -> {
            LOGGER.info("Starting route for session: {}", sessionId);
            context.addRoutes(getConsumerRouteBuilder(sessionId, newBrokers, filter));

            // Create producer route if do not exists for brokers
            if (context.getRoute(getRouteName(newBrokers)) == null) {
                context.addRoutes(getProducerRouteBuilder(newBrokers));
            }
        }).onFailure(throwable -> LOGGER.error("", throwable));
    }

    @ConsumeEvent(value = BROKERS_ADDRESS_STOP, blocking = true)
    void stopRoutes(String sessionId) {
        Try.run(() -> {
            LOGGER.info("Stopping route for session: {}", sessionId);
            ((FastCamelContext) context).stopRoute(sessionId);
            context.removeRoute(sessionId);
        }).onFailure(throwable -> LOGGER.error("", throwable));
    }

    @ConsumeEvent(value = MESSAGE_ADDRESS, blocking = true)
    void processMessage(KEventOut kEventOut) {
        LOGGER.info("Processing: {}", kEventOut);
        producer.sendBodyAndHeaders(
                getEndpointName(kEventOut.broker),
                kEventOut.value,
                Map.of(
                        KafkaConstants.KEY, kEventOut.key,
                        KafkaConstants.TOPIC, kEventOut.topic
                ));
    }

    private RouteBuilder getConsumerRouteBuilder(String sessionId, String brokers, String filter) {
        return new EndpointRouteBuilder() {
            @Override
            public void configure() throws Exception {
                EndpointConsumerBuilder kafka = kafka(filter)
                        .groupId(sessionId)
                        .brokers(brokers)
                        .topicIsPattern(true)
                        .autoOffsetReset("earliest");

                from(kafka)
                        .routeId(sessionId)
                        .process(CamelService.this::process)
                        .toD("vertx:" + sessionId);
            }
        };
    }

    private RouteBuilder getProducerRouteBuilder(String brokers) {
        return new EndpointRouteBuilder() {
            @Override
            public void configure() throws Exception {
                from(getEndpointName(brokers)).routeId(getRouteName(brokers))
                        .log("${headers}")
                        .log("${body}")
                        .toD("kafka:${header.kafka.TOPIC}?brokers=" + brokers);
            }
        };
    }

    private void process(Exchange exchange) {
        Message message = exchange.getIn();
        JsonObject json = new JsonObject()
                .put("id", message.getHeader(KafkaConstants.TOPIC, String.class)
                        + ":" + message.getHeader(KafkaConstants.PARTITION, Long.class)
                        + ":" + message.getHeader(KafkaConstants.OFFSET, Long.class))
                .put("topic", message.getHeader(KafkaConstants.TOPIC, String.class))
                .put("partition", message.getHeader(KafkaConstants.PARTITION, Long.class))
                .put("offset", message.getHeader(KafkaConstants.OFFSET, Long.class))
//                .put("timestamp", Instant.ofEpochMilli(exchange.getIn().getHeader(KafkaConstants.TIMESTAMP, Long.class)))
                .put("timestamp", DateTimeFormatter.ISO_INSTANT.format(Instant.ofEpochMilli(exchange.getIn().getHeader(KafkaConstants.TIMESTAMP, Long.class)).atZone(ZoneId.systemDefault())))
                .put("key", message.getHeader(KafkaConstants.KEY, String.class))
                .put("value", message.getBody(String.class));
        exchange.getIn().setBody(json);
    }

    private String getRouteName(String brokers){
        return "Route-" + brokers.hashCode();
    }

    private String getEndpointName(String brokers){
        return "direct:" + brokers.hashCode();
    }
}
