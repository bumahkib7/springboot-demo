package dev.runixcloud.demo.service;

import dev.runixcloud.demo.config.KafkaConfig;
import dev.runixcloud.demo.model.KafkaMessage;
import dev.runixcloud.demo.repository.KafkaMessageRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class KafkaConsumerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerService.class);

    private final KafkaMessageRepository repo;

    public KafkaConsumerService(KafkaMessageRepository repo) {
        this.repo = repo;
    }

    @KafkaListener(topics = KafkaConfig.TOPIC)
    public void consume(ConsumerRecord<String, String> record) {
        log.info("Received from Kafka: key={}, value={}", record.key(), record.value());

        KafkaMessage msg = new KafkaMessage();
        msg.setTopic(record.topic());
        msg.setKey(record.key());
        msg.setPayload(record.value());
        msg.setDirection("RECEIVED");
        msg.setTimestamp(Instant.now());
        repo.save(msg);
    }
}
