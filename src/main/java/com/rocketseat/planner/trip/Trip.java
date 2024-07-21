package com.rocketseat.planner.trip;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "trips")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Trip {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;
  @Column(nullable = false)
  private String destination;

  @Column(name = "owner_name", nullable = false)
  private String ownerName;

  @Column(name = "owner_email", nullable = false)
  private String ownerEmail;

  @Column(name = "is_confirmed", nullable = false)
  private boolean isConfirmed;

  @Column(name = "starts_at", nullable = false)
  private LocalDateTime startsAt;

  @Column(name = "ends_at", nullable = false)
  private LocalDateTime endsAt;

  public Trip(TripRequestPayloadDto data) {
    this.destination = data.getDestination();
    this.isConfirmed = false;
    this.ownerName = data.getOwnerName();
    this.ownerEmail = data.getOwnerEmail();
    this.startsAt = LocalDateTime.parse(data.getStartsAt(), DateTimeFormatter.ISO_DATE_TIME);
    this.endsAt = LocalDateTime.parse(data.getEndsAt(), DateTimeFormatter.ISO_DATE_TIME);
  }

  // metodo para confirmar particiapação na viagem
  public void confirmParticipation() {
    this.isConfirmed = true;
  }
}
