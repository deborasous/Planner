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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rocketseat.planner.common.ApiResponse;
import com.rocketseat.planner.common.ValidationService;
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
      ValidationService.emailValidators(payload.getEmailsToInvite());
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

  // retorna os dados da viagem quando passado o id da viagem como parametro da
  // url
  @GetMapping("/{tripId}")
  public ResponseEntity<?> getTripById(@PathVariable UUID tripId) {
    Optional<Trip> tripOptional = this.repository.findById(tripId);

    if (tripOptional.isPresent()) {
      return ResponseEntity.ok(tripOptional.get());
    } else {
      ApiResponse errorResponse = new ApiResponse("Viagem não encontrada para o ID: " + tripId);
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
  }

  // retorna todas as trips de um owner a partir do email
  @GetMapping
  public ResponseEntity<?> getAllTrips(@RequestParam(required = false) String ownerEmail) {
    List<Trip> trips;

    if (ownerEmail != null) {
      trips = this.repository.findByOwnerEmail(ownerEmail);
      if (trips.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ApiResponse("Verifique o e-mail " + ownerEmail + " e tente novamente"));
      }
    } else {
      trips = this.repository.findAll();
    }

    return ResponseEntity.ok(trips);
  }

  // Altera os dados da viagem
  @PutMapping("/{tripId}")
  public ResponseEntity<?> updateTrip(@PathVariable UUID tripId, @Valid @RequestBody TripRequestPayloadDto payload,
      BindingResult result) {

    if (result.hasErrors()) {
      return ResponseEntity.badRequest().body(ValidationUtil.getErrorResponse(result));
    }

    try {
      ValidationService.emailValidator(payload.getOwnerEmail());
    } catch (ValidationException e) {
      return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage()));
    }

    return this.repository.findById(tripId)
        .<ResponseEntity<?>>map(existingTrip -> {
          Trip updatedTrip = existingTrip.toBuilder()
              .ownerName(payload.getOwnerName())
              .ownerEmail(payload.getOwnerEmail())
              .destination(payload.getDestination())
              .startsAt(existingTrip.getStartsAt())
              .endsAt(existingTrip.getEndsAt())
              .build();

          this.repository.save(updatedTrip);
          return ResponseEntity.ok(updatedTrip);
        })
        .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ApiResponse("Viagem não encontrada para o ID: " + tripId)));
  }

  @GetMapping("/{tripId}/confirm")
  public ResponseEntity<?> confirmTrip(@PathVariable UUID tripId) {
    return this.repository.findById(tripId)
    .<ResponseEntity<?>>map(trip->{
      trip.setConfirmed(true);
      this.repository.save(trip);
      this.participantService.triggerConfirmEmailToParticipant(tripId);
      return ResponseEntity.ok(trip);
    })
    .orElseGet(()->ResponseEntity.status(HttpStatus.NOT_FOUND)
    .body(new ApiResponse("Viagem não encontrada para o ID: " + tripId)));
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