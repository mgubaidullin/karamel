package one.entropy.karamel.kafka;

import io.vavr.control.Try;
import one.entropy.karamel.data.KEventOut;
import one.entropy.karamel.ui.KaramelSession;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import java.util.Map;
import java.util.Objects;

@SessionScoped
public class KaramelProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(KaramelProducer.class.getCanonicalName());

    private KafkaProducer<String, String> producer;

    private String producerBrokers;

    @Inject
    KaramelSession session;

    public void create() {
        if (producer != null && Objects.equals(session.getBrokers(), producerBrokers)) {
            LOGGER.info("Reuse producer for : {}", producerBrokers);
        } else if (producer != null && !Objects.equals(session.getBrokers(), producerBrokers)) {
            close();
            initialize(session.getBrokers());
        } else {
            initialize(session.getBrokers());
        }
    }

    private void initialize(String brokers) {
        LOGGER.info("Create producer for : {}", brokers);
        producerBrokers = brokers;
        Try.run(() -> {
            Map<String, String> config = Map.of(
                    ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, producerBrokers,
                    ProducerConfig.CLIENT_ID_CONFIG, producerBrokers,
                    ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer",
                    ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer",
                    ProducerConfig.ACKS_CONFIG, "1"
            );
            producer = new KafkaProducer(config);
        }).onFailure(throwable -> LOGGER.error("", throwable));
    }

    public void publish(KEventOut kevent) {
        LOGGER.info("Publish: to {}", kevent);
        Try.run(() -> {
            ProducerRecord<String, String> data = new ProducerRecord(kevent.topic, kevent.key, kevent.value);
            producer.send(data);
        }).onFailure(throwable -> LOGGER.error("", throwable));
    }

    @PreDestroy
    void close() {
        Try.run(() -> {
            producer.close();
            LOGGER.info("Producer closed for : {}", producerBrokers);
        }).onFailure(throwable -> LOGGER.error("", throwable));
    }
}
