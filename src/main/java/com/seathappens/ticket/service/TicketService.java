package com.seathappens.ticket.service;

import com.seathappens.common.exception.BusinessException;
import com.seathappens.common.exception.ErrorCode;
import com.seathappens.common.exception.ResourceNotFoundException;
import com.seathappens.order.entity.Order;
import com.seathappens.order.repository.OrderRepository;
import com.seathappens.ticket.dto.response.TicketResponse;
import com.seathappens.ticket.entity.Ticket;
import com.seathappens.ticket.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final OrderRepository orderRepository;
    private final TicketCodeGenerator ticketCodeGenerator;

    @Transactional
    public void issueTickets(Order order) {
        if (ticketRepository.existsByOrderId(order.getId())) {
            throw new BusinessException(ErrorCode.TICKET_ALREADY_ISSUED);
        }

        int quantity = order.getReservation().getQuantity();

        List<Ticket> tickets = new ArrayList<>();

        for (int i = 0; i < quantity; i++) {
            Ticket ticket = Ticket.builder()
                    .order(order)
                    .ticketType(order.getReservation().getTicketType())
                    .ticketCode(ticketCodeGenerator.generate())
                    .build();

            tickets.add(ticket);
        }

        ticketRepository.saveAll(tickets);
    }

    @Transactional(readOnly = true)
    public TicketResponse getTicketById(UUID id) {
        return ticketRepository.findById(id)
                .map(TicketResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.TICKET_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> getTickets() {
        return ticketRepository.findAll()
                .stream()
                .map(TicketResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> getTicketsByOrderId(UUID orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new ResourceNotFoundException(ErrorCode.ORDER_NOT_FOUND);
        }

        return ticketRepository.findByOrderId(orderId)
                .stream()
                .map(TicketResponse::from)
                .toList();
    }

}
