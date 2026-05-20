package com.seathappens.outbox.repository;

import com.seathappens.outbox.entity.ProcessedKafkaEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessedKafkaEventRepository extends JpaRepository<ProcessedKafkaEvent, UUID> {

    boolean existsByEventId(UUID eventId);

}
