package com.rocketseat.planner.participants;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ParticipantPayloadDto(
    @NotBlank(message = "Nome não pode ser vazio") String name,
    @NotBlank(message = "E-mail não pode ser vazio") @Email(message = "E-mail deve ser válido") String email,
    boolean isConfirmed) {

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  public boolean getIsConfirmed() {
    return isConfirmed;
  }
}