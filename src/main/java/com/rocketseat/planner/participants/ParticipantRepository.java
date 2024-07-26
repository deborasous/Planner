package com.rocketseat.planner.participants;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, UUID> {
  Optional<Participant> findByEmail(String email);
  List<Participant> findByTripId(UUID tripId);
}
