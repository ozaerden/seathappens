package com.seathappens.order.scheduler;

import com.seathappens.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderExpiryScheduler {

    private final OrderService orderService;

    @Scheduled(fixedRateString = "${seathappens.order.expiry-scheduler-fixed-rate-ms}")
    public void expirePendingPaymentOrders() {
        int expiredCount = orderService.expirePendingPaymentOrders();

        if (expiredCount > 0) {
            log.info("Expired {} pending payment order(s).", expiredCount);
        }
    }

}
