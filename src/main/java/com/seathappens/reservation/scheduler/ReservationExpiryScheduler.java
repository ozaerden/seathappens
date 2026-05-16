package com.seathappens.reservation.scheduler;

import com.seathappens.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationExpiryScheduler {

    private final ReservationService reservationService;

    @Scheduled(fixedRateString = "${seathappens.reservation.expiry-scheduler-fixed-rate-ms}")
    public void expireReservations() {
        int expiredCount = reservationService.expireReservations();

        if (expiredCount > 0) {
            log.info("Expired {} reservation(s).", expiredCount);
        }
    }

}
