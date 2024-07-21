package com.rocketseat.planner.participants;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class ParticipantService {
  public void registerParticipantsToEvent(List<String> participantToInvite, UUID tripId) {

  }

  // recupera todos os participantes de uma viagem e envia os emails para eles 
  public void triggerConfirmEmailToParticipant(UUID tripId) {

  }
}