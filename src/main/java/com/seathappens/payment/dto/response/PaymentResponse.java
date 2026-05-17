package com.seathappens.payment.dto.response;

import com.seathappens.payment.entity.Payment;
import com.seathappens.payment.entity.PaymentProvider;
import com.seathappens.payment.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        UUID orderId,
        BigDecimal amount,
        PaymentStatus status,
        PaymentProvider provider,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getProvider(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }

}
