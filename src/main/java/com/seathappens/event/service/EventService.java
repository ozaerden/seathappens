package com.seathappens.event.service;

import com.seathappens.common.exception.ErrorCode;
import com.seathappens.common.exception.ResourceNotFoundException;
import com.seathappens.event.dto.request.CreateEventRequest;
import com.seathappens.event.dto.response.EventResponse;
import com.seathappens.event.entity.Event;
import com.seathappens.event.repository.EventRepository;
import com.seathappens.venue.entity.Venue;
import com.seathappens.venue.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;

    @Transactional
    public EventResponse createEvent(CreateEventRequest request) {
        Venue venue = venueRepository.findById(request.venueId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.VENUE_NOT_FOUND));

        Event event = Event.builder()
                .name(request.name())
                .description(request.description())
                .startDateTime(request.startDateTime())
                .endDateTime(request.endDateTime())
                .venue(venue)
                .build();

        return EventResponse.from(eventRepository.save(event));
    }

    @Transactional(readOnly = true)
    public EventResponse getEventById(UUID id) {
        return eventRepository.findById(id)
                .map(EventResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.EVENT_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getEvents() {
        return eventRepository.findAll()
                .stream()
                .map(EventResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getEventsByVenueId(UUID venueId) {
        if (!venueRepository.existsById(venueId)) {
            throw new ResourceNotFoundException(ErrorCode.VENUE_NOT_FOUND);
        }

        return eventRepository.findByVenueId(venueId)
                .stream()
                .map(EventResponse::from)
                .toList();
    }

}
