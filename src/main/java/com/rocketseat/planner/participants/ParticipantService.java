package com.rocketseat.planner.participants;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rocketseat.planner.trip.Trip;

@Service
public class ParticipantService {

  @Autowired
  private ParticipantRepository participantRepository;

  public void registerParticipantsToEvent(List<String> participantsToInvite, Trip trip) {

    List<Participant> participants = participantsToInvite.stream()
        .map(email -> new Participant(email, trip))
        .toList();

    if (!participants.isEmpty()) {
      System.out.println("Participantes criados: " + participants);
      this.participantRepository.saveAll(participants);

      List<Participant> savedParticipants = participantRepository.findAllById(participants.stream()
          .map(Participant::getId)
          .collect(Collectors.toList()));
      if (!savedParticipants.isEmpty()) {
        System.out.println("ID do primeiro participante: " + savedParticipants.get(0).getId());
      }
    }
  }

  public ParticipantCreateResponse registerParticipantToEvent(String email, Trip trip) {
    Participant newParticipant = new Participant(email, trip);
    participantRepository.save(newParticipant);

    return new ParticipantCreateResponse(newParticipant.getId());
  }

  public void triggerConfirmEmailToParticipant(String email) {

  }

  public List<String> getParticipantEmailsTripId(UUID tripId) {
    return participantRepository.findByTripId(tripId).stream().map(Participant::getEmail)
        .collect(Collectors.toList());
  }

  public ParticipantCreateResponse confirmParticipant(UUID participantId, ParticipantPayloadDto payload){
    Participant rawParticipant =participantRepository.findById(participantId)
    .orElseThrow(()-> new ParticipantNotFoundException("Participante não encontrado para o ID: " + participantId));

      rawParticipant.setConfirmed(true);
      rawParticipant.setName(payload.name());

      participantRepository.save(rawParticipant);

      return new ParticipantCreateResponse(rawParticipant.getId());
  }
}