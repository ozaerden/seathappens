package com.seathappens.event.controller;

import com.seathappens.event.dto.request.CreateEventRequest;
import com.seathappens.event.dto.response.EventResponse;
import com.seathappens.event.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Event", description = "Event management APIs")
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @Operation(summary = "Create new event.")
    @PostMapping(version = "1")
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponse createEvent(@Valid @RequestBody CreateEventRequest request) {
        return eventService.createEvent(request);
    }

    @Operation(summary = "Get event by id.")
    @GetMapping(value = "/{id}", version = "1")
    public EventResponse getEventById(@PathVariable UUID id) {
        return eventService.getEventById(id);
    }

    @Operation(summary = "List all events.")
    @GetMapping(version = "1")
    public List<EventResponse> getEvents() {
        return eventService.getEvents();
    }

    @Operation(summary = "List events by venue id.")
    @GetMapping(value = "/by-venue/{venueId}", version = "1")
    public List<EventResponse> getEventsByVenueId(@PathVariable UUID venueId) {
        return eventService.getEventsByVenueId(venueId);
    }

}
