package com.seathappens.venue.entity;

import com.seathappens.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "venues")
public class Venue extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 100)
    private String country;

    @Column(nullable = false)
    private Integer capacity;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private VenueStatus status = VenueStatus.ACTIVE;

}
