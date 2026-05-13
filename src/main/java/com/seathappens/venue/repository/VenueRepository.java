package com.seathappens.venue.repository;

import com.seathappens.venue.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VenueRepository extends JpaRepository<Venue, UUID> {

}
