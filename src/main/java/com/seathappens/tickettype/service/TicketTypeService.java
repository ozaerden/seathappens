package com.seathappens.tickettype.service;

import com.seathappens.common.exception.ErrorCode;
import com.seathappens.common.exception.ResourceNotFoundException;
import com.seathappens.event.entity.Event;
import com.seathappens.event.repository.EventRepository;
import com.seathappens.tickettype.dto.request.CreateTicketTypeRequest;
import com.seathappens.tickettype.dto.response.TicketTypeResponse;
import com.seathappens.tickettype.entity.TicketType;
import com.seathappens.tickettype.repository.TicketTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketTypeService {

    private final TicketTypeRepository ticketTypeRepository;
    private final EventRepository eventRepository;

    @Transactional
    public TicketTypeResponse createTicketType(CreateTicketTypeRequest request) {
        Event event = eventRepository.findById(request.eventId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.EVENT_NOT_FOUND));

        TicketType ticketType = TicketType.builder()
                .event(event)
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .totalQuantity(request.totalQuantity())
                .build();

        return TicketTypeResponse.from(ticketTypeRepository.save(ticketType));
    }

    @Transactional(readOnly = true)
    public TicketTypeResponse getTicketTypeById(UUID id) {
        return ticketTypeRepository.findById(id)
                .map(TicketTypeResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.TICKET_TYPE_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<TicketTypeResponse> getTicketTypes() {
        return ticketTypeRepository.findAll()
                .stream()
                .map(TicketTypeResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TicketTypeResponse> getTicketTypesByEventId(UUID eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException(ErrorCode.EVENT_NOT_FOUND);
        }

        return ticketTypeRepository.findByEventId(eventId)
                .stream()
                .map(TicketTypeResponse::from)
                .toList();
    }

}
