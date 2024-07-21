package com.rocketseat.planner.trip;

public class TripResponse {

  private String message;

  public TripResponse(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
