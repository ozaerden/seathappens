package com.seathappens.inventory.entity;

import com.seathappens.common.entity.BaseEntity;
import com.seathappens.tickettype.entity.TicketType;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "inventories")
public class Inventory extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_type_id", nullable = false, unique = true)
    private TicketType ticketType;

    @Column(nullable = false)
    private Integer totalQuantity;

    @Column(nullable = false)
    private Integer availableQuantity;

    @Column(nullable = false)
    private Integer reservedQuantity;

    @Column(nullable = false)
    private Integer soldQuantity;

}
