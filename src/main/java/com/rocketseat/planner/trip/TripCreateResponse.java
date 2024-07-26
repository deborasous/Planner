package com.rocketseat.planner.trip;

import java.util.List;
import java.util.UUID;

public class TripCreateResponse {
  private UUID tripId;
  private String ownerName;
  private String ownerEmail;
  private List<String> participantEmails;

  public TripCreateResponse(UUID tripId, String ownerName, String ownerEmail, List<String> participantEmails) {
    this.tripId = tripId;
    this.ownerName = ownerName;
    this.ownerEmail = ownerEmail;
    this.participantEmails = participantEmails;
  }

  public UUID getTripId() {
    return tripId;
  }

  public String getOwnerName() {
    return ownerName;
  }

  public String getOwnerEmail() {
    return ownerEmail;
  }

  public List<String> getParticipantEmails() {
    return participantEmails;
  }
}
