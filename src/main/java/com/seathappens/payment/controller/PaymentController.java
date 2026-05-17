package com.seathappens.payment.controller;

import com.seathappens.payment.dto.request.ProcessPaymentRequest;
import com.seathappens.payment.dto.response.PaymentResponse;
import com.seathappens.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Payment", description = "Payment management APIs")
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "Process payment.")
    @PostMapping(version = "1")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse processPayment(@Valid @RequestBody ProcessPaymentRequest request) {
        return paymentService.processPayment(request);
    }

    @Operation(summary = "Get payment by id.")
    @GetMapping(value = "/{id}", version = "1")
    public PaymentResponse getPaymentById(@PathVariable UUID id) {
        return paymentService.getPaymentById(id);
    }

    @Operation(summary = "List all payments.")
    @GetMapping(version = "1")
    public List<PaymentResponse> getPayments() {
        return paymentService.getPayments();
    }

}
