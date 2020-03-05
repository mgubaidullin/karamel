//package one.entropy.karamel.route;
//
//import io.vertx.reactivex.core.eventbus.EventBus;
//import one.entropy.karamel.data.KaramelMessage;
//import org.apache.camel.Exchange;
//import org.apache.camel.builder.RouteBuilder;
//import org.apache.camel.component.kafka.KafkaConstants;
//
//import javax.inject.Inject;
//import javax.inject.Singleton;
//
//@Singleton
//public class KaramelRoute extends RouteBuilder {
//
//    @Inject
//    EventBus bus;
//
//    @Override
//    public void configure() throws Exception {
////        from("kafka:.*?topicIsPattern=true&autoOffsetReset=earliest").autoStartup(false)
////                .routeId(CONSUMER_ROUTE_ID)
////                .process(this::processIncoming);
//
//        from("direct:message").autoStartup(false)
//                .process(this::processOutgoing)
//                .toD("kafka:${header.kafka.TOPIC}");
//    }
//
//    private void processIncoming(Exchange exchange) {
////        KaramelMessage message = new KaramelMessage(
////                exchange.getIn().getHeader(KafkaConstants.TOPIC, String.class),
////                exchange.getIn().getHeader(KafkaConstants.PARTITION, Long.class),
////                exchange.getIn().getHeader(KafkaConstants.OFFSET, Long.class),
////                Instant.ofEpochMilli(exchange.getIn().getHeader(KafkaConstants.TIMESTAMP, Long.class)),
////                exchange.getIn().getHeader(KafkaConstants.KEY, String.class),
////                exchange.getIn().getBody(String.class)
////        );
////        bus.publish("kmessage", message);
//    }
//
//    private void processOutgoing(Exchange exchange) {
//        KaramelMessage message = exchange.getIn().getBody(KaramelMessage.class);
//        exchange.getIn().setHeader(KafkaConstants.TOPIC, message.getTopic());
//        exchange.getIn().setHeader(KafkaConstants.OFFSET, message.getOffset());
//        exchange.getIn().setHeader(KafkaConstants.KEY, message.getKey());
//        exchange.getIn().setBody(message.getValue());
//    }
//}
