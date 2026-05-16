package com.seathappens.inventory.service;

import com.seathappens.common.exception.ErrorCode;
import com.seathappens.common.exception.ResourceNotFoundException;
import com.seathappens.inventory.dto.response.InventoryResponse;
import com.seathappens.inventory.repository.InventoryRepository;
import com.seathappens.tickettype.repository.TicketTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final TicketTypeRepository ticketTypeRepository;

    @Transactional(readOnly = true)
    public InventoryResponse getInventoryById(UUID id) {
        return inventoryRepository.findById(id)
                .map(InventoryResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.INVENTORY_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<InventoryResponse> getInventories() {
        return inventoryRepository.findAll()
                .stream()
                .map(InventoryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public InventoryResponse getInventoryByTicketTypeId(UUID ticketTypeId) {
        if (!ticketTypeRepository.existsById(ticketTypeId)) {
            throw new ResourceNotFoundException(ErrorCode.TICKET_TYPE_NOT_FOUND);
        }

        return inventoryRepository.findByTicketTypeId(ticketTypeId)
                .map(InventoryResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.INVENTORY_NOT_FOUND));
    }

}