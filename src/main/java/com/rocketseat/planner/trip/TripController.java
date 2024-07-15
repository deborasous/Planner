package com.rocketseat.planner.trip;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rocketseat.planner.participants.ParticipantService;

@RestController
@RequestMapping("/trips")
public class TripController {
  @Autowired
  private ParticipantService participantService;

  @Autowired
  private TripRepository repository;

  @PostMapping
  public ResponseEntity<TripCreateResponse> createTrip(@RequestBody TripRequestPayload payload) {
    Trip newTrip = new Trip(payload);

    this.repository.save(newTrip);

    this.participantService.registerParticipantsToEvent(payload.emails_to_invite(), newTrip.getId());

    return ResponseEntity.ok(new TripCreateResponse(newTrip.getId()));
  }

  @GetMapping("/{id}")
  public ResponseEntity<Trip> getTripById(@PathVariable UUID id) {
    Optional<Trip> trip = repository.findById(id);
    if (trip.isPresent()) {
      return ResponseEntity.ok(trip.get());
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping
  public ResponseEntity<List<Trip>> getAllTrips() {
    List<Trip> trips = repository.findAll();
    return ResponseEntity.ok(trips);
  }

  @PutMapping("/{tripId}")
  public ResponseEntity<Trip> updateTrip(@PathVariable UUID tripId, @RequestBody Trip tripDetails) {
    return repository.findById(tripId)
        .map(existingTrip -> {
          Trip updateTrip = existingTrip.toBuilder()
              .ownerName(tripDetails.getOwnerName())
              .ownerEmail(tripDetails.getOwnerEmail())
              .destination(tripDetails.getDestination())
              .isConfirmed(tripDetails.isConfirmed())
              .startsAt(tripDetails.getStartsAt())
              .endsAt(tripDetails.getEndsAt())
              .build();
          repository.save(updateTrip);
          return ResponseEntity.ok(updateTrip);
        })
        .orElseGet(() -> ResponseEntity.notFound().build());
    // if (trip.isPresent()) {
    // Trip tripExisting = trip.get();
    // tripExisting.setOwnerName(tripDetails.getOwnerName());
    // tripExisting.setOwnerEmail(tripDetails.getOwnerEmail());
    // tripExisting.setDestination(tripDetails.getDestination());
    // tripExisting.setConfirmed(tripDetails.isConfirmed());
    // tripExisting.setStartsAt(tripDetails.getStartsAt());
    // tripExisting.setEndsAt(tripDetails.getEndsAt());

    // repository.save(tripExisting);
    // return ResponseEntity.ok(tripExisting);
    // } else {
    // return ResponseEntity.notFound().build();
    // }
  }
}
