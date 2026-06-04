package com.seathappens.payment.service;

import com.seathappens.audit.event.AuditAction;
import com.seathappens.audit.service.AuditEventPublisher;
import com.seathappens.common.exception.BusinessException;
import com.seathappens.common.exception.ErrorCode;
import com.seathappens.common.exception.ResourceNotFoundException;
import com.seathappens.inventory.entity.Inventory;
import com.seathappens.inventory.repository.InventoryRepository;
import com.seathappens.order.entity.Order;
import com.seathappens.order.entity.OrderStatus;
import com.seathappens.order.repository.OrderRepository;
import com.seathappens.outbox.service.OutboxEventService;
import com.seathappens.payment.dto.request.ProcessPaymentRequest;
import com.seathappens.payment.dto.response.PaymentResponse;
import com.seathappens.payment.entity.Payment;
import com.seathappens.payment.entity.PaymentStatus;
import com.seathappens.payment.event.PaymentSucceededEvent;
import com.seathappens.payment.repository.PaymentRepository;
import com.seathappens.security.service.CurrentUserService;
import com.seathappens.ticket.service.TicketService;
import com.seathappens.user.entity.User;
import com.seathappens.user.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final InventoryRepository inventoryRepository;
    private final TicketService ticketService;
    private final OutboxEventService outboxEventService;
    private final CurrentUserService currentUserService;
    private final AuditEventPublisher auditEventPublisher;

    @Transactional
    public PaymentResponse processPayment(ProcessPaymentRequest request) {
        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ORDER_NOT_FOUND));

        validateOrderVisibleToCurrentUser(order);

        if (OrderStatus.PAID.equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.ORDER_ALREADY_PAID);
        }

        if (!OrderStatus.PENDING_PAYMENT.equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.ORDER_NOT_PAYABLE);
        }

        Payment payment = Payment.builder()
                .order(order)
                .amount(order.getTotalAmount())
                .build();

        if (request.success()) {
            Inventory inventory = inventoryRepository.findByTicketTypeId(order.getReservation().getTicketType().getId())
                    .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.INVENTORY_NOT_FOUND));

            payment.setStatus(PaymentStatus.SUCCESS);
            order.setStatus(OrderStatus.PAID);

            inventory.setReservedQuantity(inventory.getReservedQuantity() - order.getReservation().getQuantity());
            inventory.setSoldQuantity(inventory.getSoldQuantity() + order.getReservation().getQuantity());

            ticketService.issueTickets(order);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }

        Payment savedPayment = paymentRepository.save(payment);

        if (PaymentStatus.SUCCESS.equals(savedPayment.getStatus())) {
            outboxEventService.saveEvent(
                    "Payment",
                    savedPayment.getId().toString(),
                    "PAYMENT_SUCCEEDED",
                    new PaymentSucceededEvent(
                            savedPayment.getId(),
                            order.getId(),
                            savedPayment.getAmount(),
                            LocalDateTime.now()
                    )
            );
        }

        auditEventPublisher.publish(
                AuditAction.PAYMENT_PROCESSED,
                order.getUser().getId(),
                "Payment",
                savedPayment.getId().toString(),
                Map.of(
                        "orderId", order.getId().toString(),
                        "status", savedPayment.getStatus().name(),
                        "amount", savedPayment.getAmount().toString()
                )
        );

        return PaymentResponse.from(savedPayment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PAYMENT_NOT_FOUND));

        validatePaymentVisibleToCurrentUser(payment);

        return PaymentResponse.from(payment);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getPayments() {
        User currentUser = currentUserService.getCurrentUser();

        if (!UserRole.ADMIN.equals(currentUser.getRole())) {
            return paymentRepository.findByOrderUserId(currentUser.getId())
                    .stream()
                    .map(PaymentResponse::from)
                    .toList();
        }

        return paymentRepository.findAll()
                .stream()
                .map(PaymentResponse::from)
                .toList();
    }

    private void validatePaymentVisibleToCurrentUser(Payment payment) {
        validateOrderVisibleToCurrentUser(payment.getOrder());
    }

    private void validateOrderVisibleToCurrentUser(Order order) {
        User currentUser = currentUserService.getCurrentUser();

        if (UserRole.ADMIN.equals(currentUser.getRole())) {
            return;
        }

        if (!order.getUser().getId().equals(currentUser.getId())) {
            throw new BusinessException(ErrorCode.PAYMENT_NOT_OWNED_BY_USER);
        }
    }

}
