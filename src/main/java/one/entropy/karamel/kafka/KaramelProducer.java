package one.entropy.karamel.kafka;

import io.vavr.control.Try;
import one.entropy.karamel.data.KEventOut;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import javax.enterprise.context.SessionScoped;
import java.util.Map;
import java.util.Objects;

@SessionScoped
public class KaramelProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(KaramelProducer.class.getCanonicalName());

    private String brokers;
    private KafkaProducer<String, String> producer;

    public void create(String brokers, String sessionId) {
        if (producer != null && Objects.equals(brokers, this.brokers)) {
            LOGGER.info("Reuse producer for : {}", brokers);
        } else if (producer != null && !Objects.equals(brokers, this.brokers)) {
            close();
            initialize(brokers);
        } else {
            initialize(brokers);
        }
    }

    private void initialize(String brokers) {
        LOGGER.info("Create producer for : {}", brokers);
        this.brokers = brokers;
        Try.run(() -> {
            Map<String, String> config = Map.of(
                    ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, this.brokers,
                    ProducerConfig.CLIENT_ID_CONFIG, this.brokers,
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
            ProducerRecord<String, String> data = new ProducerRecord(kevent.getTopic(), kevent.getKey(), kevent.getValue());
            producer.send(data);
        }).onFailure(throwable -> LOGGER.error("", throwable));
    }

    @PreDestroy
    void close() {
        Try.run(() -> {
            producer.close();
            LOGGER.info("Producer closed for : {}", brokers);
        }).onFailure(throwable -> LOGGER.error("", throwable));
    }
}
