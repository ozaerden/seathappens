package com.seathappens.tickettype.controller;

import com.seathappens.tickettype.dto.request.CreateTicketTypeRequest;
import com.seathappens.tickettype.dto.response.TicketTypeResponse;
import com.seathappens.tickettype.service.TicketTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Ticket Type", description = "Ticket type management APIs")
@RestController
@RequestMapping("/api/ticket-types")
@RequiredArgsConstructor
public class TicketTypeController {

    private final TicketTypeService ticketTypeService;

    @Operation(summary = "Create new ticket type.")
    @PostMapping(version = "1")
    @ResponseStatus(HttpStatus.CREATED)
    public TicketTypeResponse createTicketType(@Valid @RequestBody CreateTicketTypeRequest request) {
        return ticketTypeService.createTicketType(request);
    }

    @Operation(summary = "Get ticket type by id.")
    @GetMapping(value = "/{id}", version = "1")
    public TicketTypeResponse getTicketTypeById(@PathVariable UUID id) {
        return ticketTypeService.getTicketTypeById(id);
    }

    @Operation(summary = "List all ticket types.")
    @GetMapping(version = "1")
    public List<TicketTypeResponse> getTicketTypes() {
        return ticketTypeService.getTicketTypes();
    }

    @Operation(summary = "List ticket types by event id.")
    @GetMapping(value = "/by-event/{eventId}", version = "1")
    public List<TicketTypeResponse> getTicketTypesByEventId(@PathVariable UUID eventId) {
        return ticketTypeService.getTicketTypesByEventId(eventId);
    }

}
