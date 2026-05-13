package com.seathappens.venue.service;

import com.seathappens.venue.dto.request.CreateVenueRequest;
import com.seathappens.venue.dto.response.VenueResponse;
import com.seathappens.venue.entity.Venue;
import com.seathappens.venue.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VenueService {

    private final VenueRepository venueRepository;

    @Transactional
    public VenueResponse createVenue(CreateVenueRequest request) {
        Venue venue = Venue.builder()
                .name(request.name())
                .city(request.city())
                .country(request.country())
                .capacity(request.capacity())
                .build();

        return VenueResponse.from(venueRepository.save(venue));
    }

    @Transactional(readOnly = true)
    public VenueResponse getVenueById(UUID id) {
        return venueRepository.findById(id)
                .map(VenueResponse::from)
                .orElseThrow(() -> new IllegalArgumentException("Venue not found"));
    }

    @Transactional(readOnly = true)
    public List<VenueResponse> getVenues() {
        return venueRepository.findAll()
                .stream()
                .map(VenueResponse::from)
                .toList();
    }

}
