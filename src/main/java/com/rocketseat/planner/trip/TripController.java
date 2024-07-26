package com.rocketseat.planner.trip;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rocketseat.planner.common.ApiResponse;
import com.rocketseat.planner.common.EmailValidator;
import com.rocketseat.planner.common.ValidationUtil;
import com.rocketseat.planner.participants.Participant;
import com.rocketseat.planner.participants.ParticipantPayloadDto;
import com.rocketseat.planner.participants.ParticipantRepository;
import com.rocketseat.planner.participants.ParticipantService;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;

@RestController
@RequestMapping("/trips")
public class TripController {
  @Autowired
  private ParticipantService participantService;

  @Autowired
  private TripRepository repository;

  @Autowired
  private ParticipantRepository participantRepository;

  @PostMapping
  public ResponseEntity<?> createTrip(@Valid @RequestBody TripRequestPayloadDto payload, BindingResult result) {
    if (result.hasErrors()) {
      return ResponseEntity.badRequest().body(ValidationUtil.getErrorResponse(result));
    }

    try {
      for (String email : payload.getEmailsToInvite()) {
        EmailValidator.validateEmail(email);
      }
    } catch (ValidationException e) {
      return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage()));
    }

    Trip newTrip = new Trip(payload);
    this.repository.save(newTrip);

    List<String> participantEmails = payload.getEmailsToInvite();
    this.participantService.registerParticipantsToEvent(participantEmails, newTrip);

    return ResponseEntity.ok(
        new TripCreateResponse(newTrip.getId(), newTrip.getOwnerName(), newTrip.getOwnerEmail(), participantEmails));
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
      return ResponseEntity.badRequest().body(ValidationUtil.getErrorResponse(result));
    }

    try {
      EmailValidator.validateEmail(payload.getOwnerEmail());
    } catch (ValidationException e) {
      return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage()));
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

  @DeleteMapping("/{tripId}")
  public ResponseEntity<?> deleteTrip(@PathVariable UUID tripId) {
    Optional<Trip> tripOptional = this.repository.findById(tripId);
    if (tripOptional.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    List<Participant> participants = this.participantRepository.findByTripId(tripId);

    List<ParticipantPayloadDto> confirmedParticipants = participants.stream()
        .filter(Participant::isConfirmed)
        .map(participant -> new ParticipantPayloadDto(
            participant.getEmail(),
            participant.getName(),
            participant.isConfirmed()))
        .toList();

    boolean hasUnconfirmedParticipants = !confirmedParticipants.isEmpty();

    if (!hasUnconfirmedParticipants) {
      this.participantRepository.deleteAll(participants);

      this.repository.deleteById(tripId);

      ApiResponse response = new ApiResponse("Viagem excluída com sucesso!", confirmedParticipants);

      return ResponseEntity.ok(response);
    } else {
      ApiResponse response = new ApiResponse(
          "A viagem não pode ser excluída porque já tem participante confirmado.", confirmedParticipants);

      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(response);
    }
  }
}