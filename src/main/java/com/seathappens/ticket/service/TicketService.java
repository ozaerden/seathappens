package com.seathappens.ticket.service;

import com.seathappens.common.exception.BusinessException;
import com.seathappens.common.exception.ErrorCode;
import com.seathappens.common.exception.ResourceNotFoundException;
import com.seathappens.order.entity.Order;
import com.seathappens.order.repository.OrderRepository;
import com.seathappens.security.service.CurrentUserService;
import com.seathappens.ticket.dto.request.ValidateTicketRequest;
import com.seathappens.ticket.dto.response.TicketResponse;
import com.seathappens.ticket.dto.response.ValidateTicketResponse;
import com.seathappens.ticket.entity.Ticket;
import com.seathappens.ticket.entity.TicketStatus;
import com.seathappens.ticket.repository.TicketRepository;
import com.seathappens.user.entity.User;
import com.seathappens.user.entity.UserRole;
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
    private final CurrentUserService currentUserService;
    private final TicketQrCodeService ticketQrCodeService;

    @Transactional
    public void issueTickets(Order order) {
        if (ticketRepository.existsByOrderId(order.getId())) {
            throw new BusinessException(ErrorCode.TICKET_ALREADY_ISSUED);
        }

        int quantity = order.getReservation().getQuantity();

        List<Ticket> tickets = new ArrayList<>();

        for (int i = 0; i < quantity; i++) {
            Ticket ticket = Ticket.builder()
                    .user(order.getUser())
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
        Ticket ticket = getVisibleTicket(id);

        return TicketResponse.from(ticket);
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> getTickets() {
        User currentUser = currentUserService.getCurrentUser();

        if (!UserRole.ADMIN.equals(currentUser.getRole())) {
            return getMyTickets();
        }

        return ticketRepository.findAll()
                .stream()
                .map(TicketResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> getTicketsByOrderId(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ORDER_NOT_FOUND));

        validateOrderVisibleToCurrentUser(order);

        return ticketRepository.findByOrderId(orderId)
                .stream()
                .map(TicketResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> getMyTickets() {
        User currentUser = currentUserService.getCurrentUser();

        return ticketRepository.findByUserId(currentUser.getId())
                .stream()
                .map(TicketResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public byte[] generateTicketQrCode(UUID id) {
        Ticket ticket = getVisibleTicket(id);

        return ticketQrCodeService.generate(ticket);
    }

    @Transactional
    public ValidateTicketResponse validateTicket(ValidateTicketRequest request) {
        User currentUser = currentUserService.getCurrentUser();

        if (!UserRole.ADMIN.equals(currentUser.getRole())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        Ticket ticket = ticketRepository.findByTicketCode(request.ticketCode())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.TICKET_NOT_FOUND));

        TicketStatus previousStatus = ticket.getStatus();

        if (!TicketStatus.ISSUED.equals(previousStatus)) {
            throw new BusinessException(ErrorCode.TICKET_NOT_ISSUED);
        }

        ticket.setStatus(TicketStatus.USED);

        return new ValidateTicketResponse(
                ticket.getId(),
                ticket.getTicketCode(),
                previousStatus,
                ticket.getStatus(),
                true
        );
    }

    private Ticket getVisibleTicket(UUID id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.TICKET_NOT_FOUND));

        validateTicketVisibleToCurrentUser(ticket);

        return ticket;
    }

    private void validateTicketVisibleToCurrentUser(Ticket ticket) {
        User currentUser = currentUserService.getCurrentUser();

        if (UserRole.ADMIN.equals(currentUser.getRole())) {
            return;
        }

        if (!ticket.getUser().getId().equals(currentUser.getId())) {
            throw new BusinessException(ErrorCode.TICKET_NOT_OWNED_BY_USER);
        }
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
