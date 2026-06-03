package com.seathappens.ticket.repository;

import com.seathappens.ticket.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    List<Ticket> findByOrderId(UUID orderId);

    boolean existsByOrderId(UUID orderId);

    List<Ticket> findByUserId(UUID userId);

    Optional<Ticket> findByTicketCode(String ticketCode);

}
