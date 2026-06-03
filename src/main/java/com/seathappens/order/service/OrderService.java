package com.seathappens.order.service;

import com.seathappens.common.exception.BusinessException;
import com.seathappens.common.exception.ErrorCode;
import com.seathappens.common.exception.ResourceNotFoundException;
import com.seathappens.inventory.entity.Inventory;
import com.seathappens.inventory.repository.InventoryRepository;
import com.seathappens.order.dto.request.CreateOrderRequest;
import com.seathappens.order.dto.response.OrderResponse;
import com.seathappens.order.entity.Order;
import com.seathappens.order.entity.OrderStatus;
import com.seathappens.order.repository.OrderRepository;
import com.seathappens.reservation.entity.Reservation;
import com.seathappens.reservation.entity.ReservationStatus;
import com.seathappens.reservation.repository.ReservationRepository;
import com.seathappens.security.service.CurrentUserService;
import com.seathappens.user.entity.User;
import com.seathappens.user.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ReservationRepository reservationRepository;
    private final InventoryRepository inventoryRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        Reservation reservation = reservationRepository.findById(request.reservationId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESERVATION_NOT_FOUND));

        if (ReservationStatus.CONVERTED.equals(reservation.getStatus())) {
            throw new BusinessException(ErrorCode.RESERVATION_ALREADY_CONVERTED);
        }

        if (!ReservationStatus.ACTIVE.equals(reservation.getStatus())) {
            throw new BusinessException(ErrorCode.RESERVATION_NOT_ACTIVE);
        }

        User currentUser = currentUserService.getCurrentUser();

        if (!reservation.getUser().getId().equals(currentUser.getId())) {
            throw new BusinessException(ErrorCode.RESERVATION_NOT_OWNED_BY_USER);
        }

        BigDecimal totalAmount = reservation.getTicketType()
                .getPrice()
                .multiply(BigDecimal.valueOf(reservation.getQuantity()));

        Order order = Order.builder()
                .user(reservation.getUser())
                .reservation(reservation)
                .totalAmount(totalAmount)
                .build();

        reservation.setStatus(ReservationStatus.CONVERTED);

        return OrderResponse.from(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ORDER_NOT_FOUND));

        validateOrderVisibleToCurrentUser(order);

        return OrderResponse.from(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrders() {
        User currentUser = currentUserService.getCurrentUser();

        if (!UserRole.ADMIN.equals(currentUser.getRole())) {
            return getMyOrders();
        }

        return orderRepository.findAll()
                .stream()
                .map(OrderResponse::from)
                .toList();
    }

    @Transactional
    public int expirePendingPaymentOrders() {
        List<Order> pendingPaymentOrders = orderRepository.findByStatus(OrderStatus.PENDING_PAYMENT);

        List<Order> expiredOrders = pendingPaymentOrders.stream()
                .filter(order -> order.getReservation().getExpiresAt().isBefore(LocalDateTime.now()))
                .toList();

        expiredOrders.forEach(this::expireOrder);

        return expiredOrders.size();
    }

    private void expireOrder(Order order) {
        Reservation reservation = order.getReservation();

        Inventory inventory = inventoryRepository.findByTicketTypeId(
                        reservation.getTicketType().getId()
                )
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.INVENTORY_NOT_FOUND));

        inventory.setAvailableQuantity(inventory.getAvailableQuantity() + reservation.getQuantity());
        inventory.setReservedQuantity(inventory.getReservedQuantity() - reservation.getQuantity());

        reservation.setStatus(ReservationStatus.EXPIRED);
        order.setStatus(OrderStatus.EXPIRED);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders() {
        User currentUser = currentUserService.getCurrentUser();

        return orderRepository.findByUserId(currentUser.getId())
                .stream()
                .map(OrderResponse::from)
                .toList();
    }

    private void validateOrderVisibleToCurrentUser(Order order) {
        User currentUser = currentUserService.getCurrentUser();

        if (UserRole.ADMIN.equals(currentUser.getRole())) {
            return;
        }

        if (!order.getUser().getId().equals(currentUser.getId())) {
            throw new BusinessException(ErrorCode.ORDER_NOT_OWNED_BY_USER);
        }
    }
    
}
