package com.seathappens.venue.dto.response;

import com.seathappens.venue.entity.Venue;
import com.seathappens.venue.entity.VenueStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record VenueResponse(
        UUID id,
        String name,
        String city,
        String country,
        Integer capacity,
        VenueStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static VenueResponse from(Venue venue) {
        return new VenueResponse(
                venue.getId(),
                venue.getName(),
                venue.getCity(),
                venue.getCountry(),
                venue.getCapacity(),
                venue.getStatus(),
                venue.getCreatedAt(),
                venue.getUpdatedAt()
        );
    }

}
