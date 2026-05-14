package com.seathappens.event.dto.response;

import com.seathappens.event.entity.Event;
import com.seathappens.event.entity.EventStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record EventResponse(

        UUID id,
        String name,
        String description,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        EventStatus status,
        UUID venueId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static EventResponse from(Event event) {
        return new EventResponse(
                event.getId(),
                event.getName(),
                event.getDescription(),
                event.getStartDateTime(),
                event.getEndDateTime(),
                event.getStatus(),
                event.getVenue().getId(),
                event.getCreatedAt(),
                event.getUpdatedAt()
        );
    }

}
