package com.seathappens.ticket.controller;

import com.seathappens.ticket.dto.request.ValidateTicketRequest;
import com.seathappens.ticket.dto.response.TicketResponse;
import com.seathappens.ticket.dto.response.ValidateTicketResponse;
import com.seathappens.ticket.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @Operation(summary = "Get ticket QR code.")
    @GetMapping(value = "/qr-code/{id}", version = "1", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getTicketQrCode(@PathVariable UUID id) {
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(ticketService.generateTicketQrCode(id));
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

    @Operation(summary = "Validate ticket.")
    @PostMapping(value = "/validate", version = "1")
    public ValidateTicketResponse validateTicket(@Valid @RequestBody ValidateTicketRequest request) {
        return ticketService.validateTicket(request);
    }

}
