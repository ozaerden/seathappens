package com.seathappens.payment.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ProcessPaymentRequest(

        @NotNull
        UUID orderId,

        @NotNull
        Boolean success

) {
}
