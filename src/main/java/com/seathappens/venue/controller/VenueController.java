package com.seathappens.venue.controller;

import com.seathappens.venue.dto.request.CreateVenueRequest;
import com.seathappens.venue.dto.response.VenueResponse;
import com.seathappens.venue.service.VenueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/venues")
@RequiredArgsConstructor
public class VenueController {

    private final VenueService venueService;

    @PostMapping(version = "1")
    @ResponseStatus(HttpStatus.CREATED)
    public VenueResponse createVenue(@Valid @RequestBody CreateVenueRequest request) {
        return venueService.createVenue(request);
    }

    @GetMapping(value = "/{id}", version = "1")
    public VenueResponse getVenueById(@PathVariable UUID id) {
        return venueService.getVenueById(id);
    }

    @GetMapping(version = "1")
    public List<VenueResponse> getVenues() {
        return venueService.getVenues();
    }
}