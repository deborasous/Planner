package com.rocketseat.planner.participants;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rocketseat.planner.trip.Trip;

@Service
public class ParticipantService {

  @Autowired
  private ParticipantRepository participantRepository;

  public void registerParticipantsToEvent(List<String> participantToInvite, Trip trip) {

    List<Participant> participants = participantToInvite.stream()
        .map(email -> new Participant(email, trip))
        .collect(Collectors.toList());

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

  public void triggerConfirmEmailToParticipant(UUID tripId) {

  }
}