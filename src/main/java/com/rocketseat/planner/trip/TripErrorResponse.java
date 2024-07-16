package com.rocketseat.planner.trip;

public class TripErrorResponse {

  private String message;

  public TripErrorResponse(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
