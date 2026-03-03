package dev.runixcloud.demo.service;

import dev.runixcloud.demo.config.KafkaConfig;
import dev.runixcloud.demo.model.KafkaMessage;
import dev.runixcloud.demo.repository.KafkaMessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class KafkaProducerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducerService.class);

    private final KafkaTemplate<String, String> kafka;
    private final KafkaMessageRepository repo;

    public KafkaProducerService(KafkaTemplate<String, String> kafka, KafkaMessageRepository repo) {
        this.kafka = kafka;
        this.repo = repo;
    }

    public void send(String payload) {
        String key = UUID.randomUUID().toString().substring(0, 8);
        kafka.send(KafkaConfig.TOPIC, key, payload);
        log.info("Sent to Kafka: key={}, payload={}", key, payload);

        KafkaMessage msg = new KafkaMessage();
        msg.setTopic(KafkaConfig.TOPIC);
        msg.setKey(key);
        msg.setPayload(payload);
        msg.setDirection("SENT");
        msg.setTimestamp(Instant.now());
        repo.save(msg);
    }
}
