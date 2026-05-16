package com.seathappens.inventory.controller;

import com.seathappens.inventory.dto.response.InventoryResponse;
import com.seathappens.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Inventory", description = "Inventory management APIs")
@RestController
@RequestMapping("/api/inventories")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @Operation(summary = "Get inventory by id.")
    @GetMapping(value = "/{id}", version = "1")
    public InventoryResponse getInventoryById(@PathVariable UUID id) {
        return inventoryService.getInventoryById(id);
    }

    @Operation(summary = "List all inventories.")
    @GetMapping(version = "1")
    public List<InventoryResponse> getInventories() {
        return inventoryService.getInventories();
    }

    @Operation(summary = "Get inventory by ticket type id.")
    @GetMapping(value = "/by-ticket-type/{ticketTypeId}", version = "1")
    public InventoryResponse getInventoryByTicketTypeId(@PathVariable UUID ticketTypeId) {
        return inventoryService.getInventoryByTicketTypeId(ticketTypeId);
    }

}
