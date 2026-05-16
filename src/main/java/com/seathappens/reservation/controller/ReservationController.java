package com.seathappens.reservation.controller;

import com.seathappens.reservation.dto.request.CreateReservationRequest;
import com.seathappens.reservation.dto.response.ReservationResponse;
import com.seathappens.reservation.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Reservation", description = "Reservation management APIs")
@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @Operation(summary = "Create new reservation.")
    @PostMapping(version = "1")
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse createReservation(@Valid @RequestBody CreateReservationRequest request) {
        return reservationService.createReservation(request);
    }

    @Operation(summary = "Get reservation by id.")
    @GetMapping(value = "/{id}", version = "1")
    public ReservationResponse getReservationById(@PathVariable UUID id) {
        return reservationService.getReservationById(id);
    }

    @Operation(summary = "List all reservations.")
    @GetMapping(version = "1")
    public List<ReservationResponse> getReservations() {
        return reservationService.getReservations();
    }

    @Operation(summary = "Cancel reservation.")
    @PostMapping(value = "/{id}/cancel", version = "1")
    public ReservationResponse cancelReservation(@PathVariable UUID id) {
        return reservationService.cancelReservation(id);
    }

}
