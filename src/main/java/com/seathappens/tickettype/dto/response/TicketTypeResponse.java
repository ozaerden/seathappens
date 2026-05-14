package com.seathappens.tickettype.dto.response;

import com.seathappens.tickettype.entity.TicketType;
import com.seathappens.tickettype.entity.TicketTypeStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TicketTypeResponse(
        UUID id,
        UUID eventId,
        String name,
        String description,
        BigDecimal price,
        Integer totalQuantity,
        TicketTypeStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static TicketTypeResponse from(TicketType ticketType) {
        return new TicketTypeResponse(
                ticketType.getId(),
                ticketType.getEvent().getId(),
                ticketType.getName(),
                ticketType.getDescription(),
                ticketType.getPrice(),
                ticketType.getTotalQuantity(),
                ticketType.getStatus(),
                ticketType.getCreatedAt(),
                ticketType.getUpdatedAt()
        );
    }

}
