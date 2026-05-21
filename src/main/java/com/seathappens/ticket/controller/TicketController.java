package com.seathappens.ticket.controller;

import com.seathappens.ticket.dto.response.TicketResponse;
import com.seathappens.ticket.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Ticket", description = "Ticket management APIs")
@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @Operation(summary = "Get ticket by id.")
    @GetMapping(value = "/{id}", version = "1")
    public TicketResponse getTicketById(@PathVariable UUID id) {
        return ticketService.getTicketById(id);
    }

    @Operation(summary = "List all tickets.")
    @GetMapping(version = "1")
    public List<TicketResponse> getTickets() {
        return ticketService.getTickets();
    }

    @Operation(summary = "List tickets by order id.")
    @GetMapping(value = "/by-order/{orderId}", version = "1")
    public List<TicketResponse> getTicketsByOrderId(@PathVariable UUID orderId) {
        return ticketService.getTicketsByOrderId(orderId);
    }

    @Operation(summary = "List my tickets.")
    @GetMapping(value = "/my", version = "1")
    public List<TicketResponse> getMyTickets() {
        return ticketService.getMyTickets();
    }

}
