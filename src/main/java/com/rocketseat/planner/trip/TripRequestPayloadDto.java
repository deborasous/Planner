package com.rocketseat.planner.trip;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record TripRequestPayloadDto(
        @NotBlank(message = "Destino não pode ser vazio") String destination,
        @NotBlank(message = "Data de início da viagem não pode ser vazio") String startsAt,
        @NotBlank(message = "Data do final da viagem pode ser vazio") String endsAt,
        List<@Email(message = "E-mail do participante deve ser válido") String> emailsToInvite,
        @NotBlank(message = "E-mail não pode ser vazio") @Email(message = "E-mail deve ser válido") String ownerEmail,
        @NotBlank(message = "Nome não pode ser vazio") String ownerName) {

    public String getDestination() {
        return destination;
    }

    public String getStartsAt() {
        return startsAt;
    }

    public String getEndsAt() {
        return endsAt;
    }

    public List<String> getEmailsToInvite() {
        return emailsToInvite != null ? emailsToInvite : List.of();
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public String getOwnerName() {
        return ownerName;
    }
}
