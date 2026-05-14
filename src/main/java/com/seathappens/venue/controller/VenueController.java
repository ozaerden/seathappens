package com.seathappens.venue.controller;

import com.seathappens.venue.dto.request.CreateVenueRequest;
import com.seathappens.venue.dto.response.VenueResponse;
import com.seathappens.venue.service.VenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Venue", description = "Venue management APIs")
@RestController
@RequestMapping("/api/venues")
@RequiredArgsConstructor
public class VenueController {

    private final VenueService venueService;

    @Operation(summary = "Create new venue.")
    @PostMapping(version = "1")
    @ResponseStatus(HttpStatus.CREATED)
    public VenueResponse createVenue(@Valid @RequestBody CreateVenueRequest request) {
        return venueService.createVenue(request);
    }

    @Operation(summary = "Get a venue by using it's ID.")
    @GetMapping(value = "/{id}", version = "1")
    public VenueResponse getVenueById(@PathVariable UUID id) {
        return venueService.getVenueById(id);
    }

    @Operation(summary = "List all venues.")
    @GetMapping(version = "1")
    public List<VenueResponse> getVenues() {
        return venueService.getVenues();
    }
}