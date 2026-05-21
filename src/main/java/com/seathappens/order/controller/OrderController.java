package com.seathappens.order.controller;

import com.seathappens.order.dto.request.CreateOrderRequest;
import com.seathappens.order.dto.response.OrderResponse;
import com.seathappens.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Order", description = "Order management APIs")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Create new order.")
    @PostMapping(version = "1")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }

    @Operation(summary = "Get order by id.")
    @GetMapping(value = "/{id}", version = "1")
    public OrderResponse getOrderById(@PathVariable UUID id) {
        return orderService.getOrderById(id);
    }

    @Operation(summary = "List all orders.")
    @GetMapping(version = "1")
    public List<OrderResponse> getOrders() {
        return orderService.getOrders();
    }

    @Operation(summary = "List my orders.")
    @GetMapping(value = "/my", version = "1")
    public List<OrderResponse> getMyOrders() {
        return orderService.getMyOrders();
    }

}
