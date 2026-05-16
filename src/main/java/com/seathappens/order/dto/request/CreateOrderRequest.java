package com.seathappens.order.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateOrderRequest(

        @NotNull
        UUID reservationId
) {
}
