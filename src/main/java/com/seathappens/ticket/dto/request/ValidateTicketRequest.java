package com.seathappens.ticket.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ValidateTicketRequest(

        @NotBlank
        String ticketCode
) {
}
