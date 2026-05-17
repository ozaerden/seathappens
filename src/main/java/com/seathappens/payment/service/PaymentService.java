package com.seathappens.payment.service;

import com.seathappens.common.exception.BusinessException;
import com.seathappens.common.exception.ErrorCode;
import com.seathappens.common.exception.ResourceNotFoundException;
import com.seathappens.inventory.entity.Inventory;
import com.seathappens.inventory.repository.InventoryRepository;
import com.seathappens.order.entity.Order;
import com.seathappens.order.entity.OrderStatus;
import com.seathappens.order.repository.OrderRepository;
import com.seathappens.payment.dto.request.ProcessPaymentRequest;
import com.seathappens.payment.dto.response.PaymentResponse;
import com.seathappens.payment.entity.Payment;
import com.seathappens.payment.entity.PaymentStatus;
import com.seathappens.payment.repository.PaymentRepository;
import com.seathappens.ticket.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final InventoryRepository inventoryRepository;
    private final TicketService ticketService;

    @Transactional
    public PaymentResponse processPayment(ProcessPaymentRequest request) {
        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ORDER_NOT_FOUND));

        if (order.getStatus() == OrderStatus.PAID) {
            throw new BusinessException(ErrorCode.ORDER_ALREADY_PAID);
        }

        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
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

        return PaymentResponse.from(paymentRepository.save(payment));
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(UUID id) {
        return paymentRepository.findById(id)
                .map(PaymentResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PAYMENT_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getPayments() {
        return paymentRepository.findAll()
                .stream()
                .map(PaymentResponse::from)
                .toList();
    }

}
