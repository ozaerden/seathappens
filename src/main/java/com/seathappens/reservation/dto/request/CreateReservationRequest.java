package com.seathappens.reservation.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateReservationRequest(

        @NotNull
        UUID ticketTypeId,

        @NotNull
        @Min(1)
        Integer quantity
) {
}
