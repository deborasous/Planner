package com.rocketseat.planner.participants;

import java.util.UUID;

public class ParticipantCreateResponse {
  private UUID participantId;

  public ParticipantCreateResponse(UUID participantId) {
    this.participantId = participantId;
  }

  public UUID getParticipantId() {
    return participantId;
  }

  public void setParticipantId(UUID participantId) {
    this.participantId = participantId;
  }
}
