package com.seathappens.ticket.dto.response;

import com.seathappens.ticket.entity.Ticket;
import com.seathappens.ticket.entity.TicketStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record TicketResponse(
        UUID id,
        UUID orderId,
        UUID ticketTypeId,
        String ticketCode,
        TicketStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static TicketResponse from(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getOrder().getId(),
                ticket.getTicketType().getId(),
                ticket.getTicketCode(),
                ticket.getStatus(),
                ticket.getCreatedAt(),
                ticket.getUpdatedAt()
        );
    }

}
