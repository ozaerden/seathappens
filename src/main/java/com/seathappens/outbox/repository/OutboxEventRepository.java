package com.seathappens.outbox.repository;

import com.seathappens.outbox.entity.OutboxEvent;
import com.seathappens.outbox.entity.OutboxEventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

    List<OutboxEvent> findTop50ByStatusOrderByCreatedAtAsc(OutboxEventStatus status);
}
