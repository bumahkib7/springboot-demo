package dev.runixcloud.demo.repository;

import dev.runixcloud.demo.model.KafkaMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KafkaMessageRepository extends JpaRepository<KafkaMessage, Long> {
    List<KafkaMessage> findTop50ByOrderByTimestampDesc();
}
