package com.rocketseat.planner.participants;

public class ParticipantNotFoundException extends RuntimeException {
  public ParticipantNotFoundException(String message) {
    super(message);
  }
}
