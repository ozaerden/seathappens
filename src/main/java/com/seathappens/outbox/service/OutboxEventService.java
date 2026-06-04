package com.seathappens.outbox.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seathappens.common.context.CorrelationIdContext;
import com.seathappens.common.exception.ErrorCode;
import com.seathappens.common.exception.InfrastructureException;
import com.seathappens.outbox.entity.OutboxEvent;
import com.seathappens.outbox.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OutboxEventService {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public void saveEvent(String aggregateType, String aggregateId, String eventType, Object payload) {
        try {
            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .aggregateType(aggregateType)
                    .aggregateId(aggregateId)
                    .eventType(eventType)
                    .correlationId(CorrelationIdContext.getCurrentCorrelationId().orElse(null))
                    .payload(objectMapper.writeValueAsString(payload))
                    .retryCount(0)
                    .build();

            outboxEventRepository.save(outboxEvent);

        } catch (JsonProcessingException exception) {
            throw new InfrastructureException(ErrorCode.OUTBOX_SERIALIZATION_ERROR, exception);
        }
    }

}
