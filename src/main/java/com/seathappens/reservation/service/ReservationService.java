package com.seathappens.reservation.service;

import com.seathappens.common.exception.BusinessException;
import com.seathappens.common.exception.ErrorCode;
import com.seathappens.common.exception.ResourceNotFoundException;
import com.seathappens.inventory.entity.Inventory;
import com.seathappens.inventory.repository.InventoryRepository;
import com.seathappens.reservation.config.ReservationProperties;
import com.seathappens.reservation.dto.request.CreateReservationRequest;
import com.seathappens.reservation.dto.response.ReservationResponse;
import com.seathappens.reservation.entity.Reservation;
import com.seathappens.reservation.entity.ReservationStatus;
import com.seathappens.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final InventoryRepository inventoryRepository;

    private final ReservationProperties reservationProperties;

    @Transactional
    public ReservationResponse createReservation(CreateReservationRequest request) {
        Inventory inventory = inventoryRepository.findByTicketTypeId(request.ticketTypeId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.INVENTORY_NOT_FOUND));

        if (inventory.getAvailableQuantity() < request.quantity()) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_INVENTORY);
        }

        inventory.setAvailableQuantity(inventory.getAvailableQuantity() - request.quantity());
        inventory.setReservedQuantity(inventory.getReservedQuantity() + request.quantity());

        Reservation reservation = Reservation.builder()
                .ticketType(inventory.getTicketType())
                .quantity(request.quantity())
                .expiresAt(LocalDateTime.now().plusMinutes(reservationProperties.durationMinutes()))
                .build();

        return ReservationResponse.from(reservationRepository.save(reservation));
    }

    @Transactional(readOnly = true)
    public ReservationResponse getReservationById(UUID id) {
        return reservationRepository.findById(id)
                .map(ReservationResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESERVATION_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservations() {
        return reservationRepository.findAll()
                .stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @Transactional
    public ReservationResponse cancelReservation(UUID id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESERVATION_NOT_FOUND));

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new BusinessException(ErrorCode.RESERVATION_ALREADY_CANCELLED);
        }

        if (reservation.getStatus() != ReservationStatus.ACTIVE) {
            throw new BusinessException(ErrorCode.RESERVATION_NOT_ACTIVE);
        }

        Inventory inventory = inventoryRepository.findByTicketTypeId(reservation.getTicketType().getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.INVENTORY_NOT_FOUND));

        inventory.setAvailableQuantity(inventory.getAvailableQuantity() + reservation.getQuantity());
        inventory.setReservedQuantity(inventory.getReservedQuantity() - reservation.getQuantity());

        reservation.setStatus(ReservationStatus.CANCELLED);

        return ReservationResponse.from(reservation);
    }

}
