package com.rocketseat.planner.trip;

import jakarta.validation.constraints.NotBlank;

public class TripConfirmationRequestDto {

  @NotBlank(message = "E-mail deve ser válido")
  private String email;

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}