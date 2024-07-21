package com.rocketseat.planner.trip;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rocketseat.planner.participants.ParticipantService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/trips")
public class TripController {
  @Autowired
  private ParticipantService participantService;

  @Autowired
  private TripRepository repository;

  @PostMapping
  public ResponseEntity<?> createTrip(@Valid @RequestBody TripRequestPayloadDto payload, BindingResult result) {
    if (result.hasErrors()) {
      return ResponseEntity.badRequest().body(getErrorResponse(result));
    }

    Trip newTrip = new Trip(payload);
    this.repository.save(newTrip);
    this.participantService.registerParticipantsToEvent(payload.emailsToInvite(), newTrip.getId());

    return ResponseEntity.ok(new TripCreateResponse(newTrip.getId()));
  }

  @GetMapping("/{tripId}")
  public ResponseEntity<Trip> getTripById(@PathVariable UUID tripId) {
    Optional<Trip> trip = this.repository.findById(tripId);
    if (trip.isPresent()) {
      return ResponseEntity.ok(trip.get());
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping
  public ResponseEntity<List<Trip>> getAllTrips() {
    List<Trip> trips = this.repository.findAll();
    return ResponseEntity.ok(trips);
  }

  @PutMapping("/{tripId}")
  public ResponseEntity<?> updateTrip(@PathVariable UUID tripId, @Valid @RequestBody TripRequestPayloadDto payload,
      BindingResult result) {
    if (result.hasErrors()) {
      return ResponseEntity.badRequest().body(getErrorResponse(result));
    }

    Optional<Trip> optionalTrip = this.repository.findById(tripId);
    if (optionalTrip.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    Trip existingTrip = optionalTrip.get();
    Trip updatedTrip = existingTrip.toBuilder()
        .ownerName(payload.getOwnerName())
        .ownerEmail(payload.getOwnerEmail())
        .destination(payload.getDestination())
        .startsAt(existingTrip.getStartsAt())
        .endsAt(existingTrip.getEndsAt())
        .build();

    repository.save(updatedTrip);
    return ResponseEntity.ok(updatedTrip);
  }

  @GetMapping("/{tripId}/confirm")
  public ResponseEntity<Trip> confirmTrip(@PathVariable UUID tripId) {
    Optional<Trip> trip = this.repository.findById(tripId);

    if (trip.isPresent()) {
      Trip rawTrip = trip.get();
      rawTrip.setConfirmed(true);

      this.repository.save(rawTrip);
      this.participantService.triggerConfirmEmailToParticipant(tripId);

      return ResponseEntity.ok(rawTrip);
    }

    return ResponseEntity.notFound().build();
  }
  
  // TripResponse.java para padronizar mensagens de erro
  private TripResponse getErrorResponse(BindingResult result) {
    StringBuilder errorMessage = new StringBuilder("Validação de erros: ");
    for (FieldError error : result.getFieldErrors()) {
      errorMessage.append(error.getField()).append(" - ").append(error.getDefaultMessage()).append("; ");
    }
    return new TripResponse(errorMessage.toString());
  }
}