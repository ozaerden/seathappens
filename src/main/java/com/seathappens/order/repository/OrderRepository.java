package com.seathappens.order.repository;

import com.seathappens.order.entity.Order;
import com.seathappens.order.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByUserId(UUID userId);

}
