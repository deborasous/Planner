package com.rocketseat.planner.trip;

public class TripNotFoundException extends RuntimeException {
  public TripNotFoundException(String message){
    super(message);
  }
}
