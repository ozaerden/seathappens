package com.seathappens.reservation;

import com.seathappens.common.exception.BusinessException;
import com.seathappens.common.exception.ErrorCode;
import com.seathappens.event.entity.Event;
import com.seathappens.event.repository.EventRepository;
import com.seathappens.inventory.entity.Inventory;
import com.seathappens.inventory.repository.InventoryRepository;
import com.seathappens.reservation.dto.request.CreateReservationRequest;
import com.seathappens.reservation.entity.Reservation;
import com.seathappens.reservation.entity.ReservationStatus;
import com.seathappens.reservation.repository.ReservationRepository;
import com.seathappens.reservation.service.ReservationService;
import com.seathappens.tickettype.entity.TicketType;
import com.seathappens.tickettype.repository.TicketTypeRepository;
import com.seathappens.venue.entity.Venue;
import com.seathappens.venue.repository.VenueRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ReservationConcurrencyTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private TicketTypeRepository ticketTypeRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test //If two reservation requests are received at the same time, doesn't that constitute overselling?
    void shouldNotOversellWhenTwoReservationsHappenConcurrently() throws Exception {
        TicketType ticketType = createTicketTypeWithInventory(1);

        CountDownLatch startLatch = new CountDownLatch(1);

        Callable<Boolean> reservationTask = () -> {
            startLatch.await();

            try {
                reservationService.createReservation(
                        new CreateReservationRequest(ticketType.getId(), 1)
                );
                return true;
            } catch (BusinessException exception) {
                assertThat(exception.getErrorCode())
                        .isEqualTo(ErrorCode.INSUFFICIENT_INVENTORY);
                return false;
            } catch (ObjectOptimisticLockingFailureException exception) {
                return false;
            }
        };

        List<Boolean> results;

        try (ExecutorService executorService = Executors.newFixedThreadPool(2)) {
            Future<Boolean> firstResult = executorService.submit(reservationTask);
            Future<Boolean> secondResult = executorService.submit(reservationTask);

            startLatch.countDown();

            results = List.of(
                    firstResult.get(5, TimeUnit.SECONDS),
                    secondResult.get(5, TimeUnit.SECONDS)
            );
        }

        assertThat(results).containsExactlyInAnyOrder(true, false);

        Inventory inventory = inventoryRepository.findByTicketTypeId(ticketType.getId())
                .orElseThrow();

        assertThat(inventory.getAvailableQuantity()).isZero();
        assertThat(inventory.getReservedQuantity()).isEqualTo(1);
        assertThat(inventory.getSoldQuantity()).isZero();

        List<Reservation> activeReservations = reservationRepository.findAll()
                .stream()
                .filter(reservation -> reservation.getTicketType().getId().equals(ticketType.getId()))
                .filter(reservation -> ReservationStatus.ACTIVE.equals(reservation.getStatus()))
                .toList();

        assertThat(activeReservations).hasSize(1);
    }

    @Test //Does the @Version in the inventory actually run optimistic lock?
    void shouldUseOptimisticLockingOnInventory() throws Exception {
        TicketType ticketType = createTicketTypeWithInventory(10);

        Inventory inventory = inventoryRepository.findByTicketTypeId(ticketType.getId())
                .orElseThrow();

        CountDownLatch bothTransactionsLoadedInventory = new CountDownLatch(2);
        CountDownLatch allowBothTransactionsToUpdate = new CountDownLatch(1);

        Callable<Boolean> updateTask = () -> {
            try {
                transactionTemplate.executeWithoutResult(ignored -> {
                    Inventory loadedInventory = inventoryRepository.findById(inventory.getId())
                            .orElseThrow();

                    bothTransactionsLoadedInventory.countDown();

                    try {
                        allowBothTransactionsToUpdate.await();
                    } catch (InterruptedException exception) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(exception);
                    }

                    loadedInventory.setAvailableQuantity(
                            loadedInventory.getAvailableQuantity() - 1
                    );
                });

                return true;
            } catch (ObjectOptimisticLockingFailureException exception) {
                return false;
            }
        };

        List<Boolean> results;

        try (ExecutorService executorService = Executors.newFixedThreadPool(2)) {
            Future<Boolean> firstResult = executorService.submit(updateTask);
            Future<Boolean> secondResult = executorService.submit(updateTask);

            boolean bothTransactionsReady = bothTransactionsLoadedInventory.await(5, TimeUnit.SECONDS);
            assertThat(bothTransactionsReady).isTrue();

            allowBothTransactionsToUpdate.countDown();

            results = List.of(
                    firstResult.get(5, TimeUnit.SECONDS),
                    secondResult.get(5, TimeUnit.SECONDS)
            );
        }

        assertThat(results).containsExactlyInAnyOrder(true, false);
    }

    private TicketType createTicketTypeWithInventory(Integer totalQuantity) {
        Venue venue = venueRepository.save(Venue.builder()
                .name("Concurrency Test Venue")
                .city("Istanbul")
                .country("Turkey")
                .capacity(1000)
                .build());

        Event event = eventRepository.save(Event.builder()
                .venue(venue)
                .name("Concurrency Test Event")
                .description("Concurrency test event")
                .startDateTime(LocalDateTime.now().plusDays(1))
                .endDateTime(LocalDateTime.now().plusDays(1).plusHours(2))
                .build());

        TicketType ticketType = ticketTypeRepository.save(TicketType.builder()
                .event(event)
                .name("Concurrency Test Ticket Type")
                .description("Only limited tickets")
                .price(BigDecimal.valueOf(100))
                .totalQuantity(totalQuantity)
                .build());

        inventoryRepository.save(Inventory.builder()
                .ticketType(ticketType)
                .totalQuantity(totalQuantity)
                .availableQuantity(totalQuantity)
                .reservedQuantity(0)
                .soldQuantity(0)
                .build());

        return ticketType;
    }
}