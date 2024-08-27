package com.rocketseat.planner.trip;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class TripCreateResponse {
  private UUID tripId;
  private String ownerName;
  private String ownerEmail;
  private String destination;
  private LocalDateTime startsAt;
  private LocalDateTime endsAt;
  private List<String> participantEmails;
  private UUID newParticipantId;

  public TripCreateResponse(UUID tripId, String ownerName, String ownerEmail, String destination,
      LocalDateTime startsAt,
      LocalDateTime endsAt, List<String> participantEmails, UUID newParticipantId) {
    this.tripId = tripId;
    this.ownerName = ownerName;
    this.ownerEmail = ownerEmail;
    this.destination = destination;
    this.startsAt = startsAt;
    this.endsAt = endsAt;
    this.participantEmails = participantEmails;
    this.newParticipantId = newParticipantId;
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

  public String getDestination() {
    return destination;
  }

  public LocalDateTime getStartsAt() {
    return startsAt;
  }

  public LocalDateTime getEndsAt() {
    return endsAt;
  }

  public List<String> getParticipantEmails() {
    return participantEmails;
  }

  public UUID getNewPartcicipantId() {
    return newParticipantId;
  }
}
