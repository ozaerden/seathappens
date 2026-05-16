package com.seathappens.inventory.dto.response;

import com.seathappens.inventory.entity.Inventory;

import java.time.LocalDateTime;
import java.util.UUID;

public record InventoryResponse(
        UUID id,
        UUID ticketTypeId,
        Integer totalQuantity,
        Integer availableQuantity,
        Integer reservedQuantity,
        Integer soldQuantity,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static InventoryResponse from(Inventory inventory) {
        return new InventoryResponse(
                inventory.getId(),
                inventory.getTicketType().getId(),
                inventory.getTotalQuantity(),
                inventory.getAvailableQuantity(),
                inventory.getReservedQuantity(),
                inventory.getSoldQuantity(),
                inventory.getCreatedAt(),
                inventory.getUpdatedAt()
        );
    }

}
