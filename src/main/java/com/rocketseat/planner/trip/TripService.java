package com.rocketseat.planner.trip;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rocketseat.planner.common.ApiResponse;
import com.rocketseat.planner.common.ValidationService;
import com.rocketseat.planner.participants.Participant;
import com.rocketseat.planner.participants.ParticipantCreateResponse;
import com.rocketseat.planner.participants.ParticipantNotFoundException;
import com.rocketseat.planner.participants.ParticipantPayloadDto;
import com.rocketseat.planner.participants.ParticipantRepository;
import com.rocketseat.planner.participants.ParticipantService;

import jakarta.transaction.Transactional;

@Service
public class TripService {

  private final TripRepository repository;
  private final ParticipantService participantService;
  private final ParticipantRepository participantRepository;

  @Autowired
  public TripService(TripRepository repository, ParticipantService participantService,
      ParticipantRepository participantRepository) {
    this.repository = repository;
    this.participantService = participantService;
    this.participantRepository = participantRepository;
  }

  @Transactional
  public TripCreateResponse createTripWithParticipants(TripRequestPayloadDto payload) {
    // Validação dos emails
    ValidationService.emailValidators(payload.getEmailsToInvite());

    // Criação do objeto Trip
    Trip newTrip = new Trip(payload);
    repository.save(newTrip);

    // Registro dos participantes
    List<String> participantEmails = payload.getEmailsToInvite();
    this.participantService.registerParticipantsToEvent(participantEmails, newTrip);

    // Retorno da resposta de criação
    return new TripCreateResponse(
        newTrip.getId(),
        newTrip.getOwnerName(),
        newTrip.getOwnerEmail(),
        newTrip.getDestination(),
        newTrip.getEndsAt(),
        newTrip.getEndsAt(),
        participantEmails,
        null);
  }

  public Trip findTripById(UUID tripId) throws TripNotFoundException {
    return repository.findById(tripId)
        .orElseThrow(() -> new TripNotFoundException("Viagem não encontrada: " + tripId));
  }

  public List<Trip> getAllTripsByOwnerEmail(String ownerEmail) {
    if (ownerEmail != null) {
      List<Trip> trips = repository.findByOwnerEmail(ownerEmail);

      if (trips.isEmpty()) {
        throw new TripNotFoundException("Verifique o e-mail " + ownerEmail + " e tente novamente");
      }
      return trips;
    } else {
      return repository.findAll();
    }
  }

  public Trip updateTrip(UUID tripId, TripRequestPayloadDto payload) {
    // Valida o e-mail do proprietário
    ValidationService.emailValidator(payload.getOwnerEmail());

    // Encontra a viagem existente
    Trip existingTrip = repository.findById(tripId)
        .orElseThrow(() -> new TripNotFoundException("Viagem não encontrada para o ID: " + tripId));

    String startAtString = payload.getStartsAt().replace("Z", "");
    String endsAtString = payload.endsAt().replace("Z", "");

    LocalDateTime startsAtDateTime = LocalDateTime.parse(startAtString, DateTimeFormatter.ISO_DATE_TIME);
    LocalDateTime endsAtDateTime = LocalDateTime.parse(endsAtString, DateTimeFormatter.ISO_DATE_TIME);

    // Atualiza a viagem com os dados fornecidos
    Trip updatedTrip = existingTrip.toBuilder()
        .ownerEmail(payload.getOwnerEmail())
        .ownerName(payload.getOwnerName())
        .destination(payload.getDestination())
        .startsAt(startsAtDateTime)
        .endsAt(endsAtDateTime)
        .build();
    // Salva a viagem atualizada
    return repository.save(updatedTrip);
  }

  public Trip confirmTrip(UUID tripId) {
    Trip trip = repository.findById(tripId)
        .orElseThrow(() -> new TripNotFoundException("Viagem não encontrada para o ID: " + tripId));

    trip.setConfirmed(true);

    repository.save(trip);

    triggerUpdateTrip(tripId);

    return trip;
  }

  public TripCreateResponse inviteParticipantToTrip(UUID tripId, ParticipantPayloadDto payload) {
    Optional<Trip> optionalTrip = repository.findById(tripId);

    if (optionalTrip.isPresent()) {
      Trip rawTrip = optionalTrip.get();

      ParticipantCreateResponse response = participantService.registerParticipantToEvent(payload.email(), rawTrip);

      if (rawTrip.isConfirmed()) {
        participantService.triggerConfirmEmailToParticipant(payload.email());
      }

      List<String> participantToInvite = participantService.getParticipantEmailsTripId(tripId);

      return new TripCreateResponse(
          rawTrip.getId(),
          rawTrip.getOwnerName(),
          rawTrip.getOwnerEmail(),
          rawTrip.getDestination(),
          rawTrip.getStartsAt(),
          rawTrip.getEndsAt(),
          participantToInvite,
          response.getParticipantId());
    }

    throw new TripNotFoundException("Viagem não encontrada para o ID: " + tripId);
  }

  public ApiResponse deleteTrip(UUID tripId) {
    Optional<Trip> tripOptional = repository.findById(tripId);

    if (tripOptional.isEmpty()) {
      return new ApiResponse("Viagem não encontrada.", null);
    }

    List<Participant> participants = participantRepository.findByTripId(tripId);

    List<ParticipantPayloadDto> confirmedParticipants = participants.stream().filter(Participant::isConfirmed)
        .map(participant -> new ParticipantPayloadDto(
            participant.getName(),
            participant.getEmail(),
            participant.isConfirmed()))
        .toList();

    boolean hasUnconfirmedParticipants = !confirmedParticipants.isEmpty();

    if (!hasUnconfirmedParticipants) {
      participantRepository.deleteAll(participants);
      repository.deleteById(tripId);

      return new ApiResponse("Viagem excluída com sucesso!", confirmedParticipants);
    } else {
      return new ApiResponse("A viagem não pode ser excluída porque já tem participante confirmado.",
          confirmedParticipants);
    }
  }

  public List<Participant> getAllParticipantsFromEvent(UUID tripId) {
    @SuppressWarnings("unused")
    Trip trip = repository.findById(tripId)
        .orElseThrow(() -> new TripNotFoundException("Viagem não encontrada para o ID: " + tripId));

    List<Participant> participants = participantRepository.findByTripId(tripId);

    if (participants.isEmpty()) {
      throw new ParticipantNotFoundException("Nenhum participante encontrado para a viagem com ID: " + tripId);
    }

    return participants;
  }

  public void triggerUpdateTrip(UUID tripId) {

  }
}
