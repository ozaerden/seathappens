package com.seathappens.payment.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentSucceededEvent(
        UUID paymentId,
        UUID orderId,
        BigDecimal amount,
        LocalDateTime occurredAt
) {
}
