package com.seathappens.ticket.dto.response;

import com.seathappens.ticket.entity.TicketStatus;

import java.util.UUID;

public record ValidateTicketResponse(
        UUID ticketId,
        String ticketCode,
        TicketStatus previousStatus,
        TicketStatus currentStatus,
        boolean valid
) {
}
