package com.seathappens.inventory.repository;

import com.seathappens.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

    Optional<Inventory> findByTicketTypeId(UUID ticketTypeId);

    boolean existsByTicketTypeId(UUID ticketTypeId);
    
}
