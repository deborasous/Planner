package com.rocketseat.planner.common;

import java.util.List;

import com.rocketseat.planner.participants.ParticipantPayloadDto;

public class ApiResponse {

  private String message;
  private List<ParticipantPayloadDto> participants;

  public ApiResponse(String message) {
    this.message = message;
  }

  public ApiResponse(String message, List<ParticipantPayloadDto> participants) {
    this.message = message;
    this.participants = participants;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public List<ParticipantPayloadDto> getParticipants() {
    return participants;
  }

  public void setParticipants(List<ParticipantPayloadDto> participants) {
    this.participants = participants;
  }
}
