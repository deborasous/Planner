package com.rocketseat.planner.trip;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rocketseat.planner.common.ApiResponse;
import com.rocketseat.planner.common.ValidationUtil;
import com.rocketseat.planner.participants.Participant;
import com.rocketseat.planner.participants.ParticipantPayloadDto;
import com.rocketseat.planner.participants.ParticipantService;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;

@RestController
@RequestMapping("/trips")
public class TripController {
  private final TripService tripService;
  private final ParticipantService participantService;

  @Autowired
  public TripController(
      TripService tripService, ParticipantService participantService) {
    this.tripService = tripService;
    this.participantService = participantService;
  }

  @PostMapping
  public ResponseEntity<?> createTrip(@Valid @RequestBody TripRequestPayloadDto payload, BindingResult result) {
    if (result.hasErrors()) {
      return ResponseEntity.badRequest().body(ValidationUtil.getErrorResponse(result));
    }

    try {
      TripCreateResponse response = tripService.createTripWithParticipants(payload);
      return ResponseEntity.ok(response);
    } catch (ValidationException e) {
      return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage()));
    }
  }

  // id da viagem como parametro da url
  @GetMapping("/{tripId}")
  public ResponseEntity<?> getTripById(@PathVariable UUID tripId) {
    try {
      Trip trip = tripService.findTripById(tripId);
      return ResponseEntity.ok(trip);
    } catch (Exception e) {
      ApiResponse errorResponse = new ApiResponse(e.getMessage());
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
  }

  @GetMapping
  public ResponseEntity<?> getAllTripsByOwnerEmail(@RequestParam(required = false) String ownerEmail) {
    try {
      List<Trip> trips = tripService.getAllTripsByOwnerEmail(ownerEmail);
      return ResponseEntity.ok(trips);
    } catch (TripNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage()));
    }
  }

  @PutMapping("/{tripId}")
  public ResponseEntity<?> updateTrip(@PathVariable UUID tripId, @Valid @RequestBody TripRequestPayloadDto payload,
      BindingResult result) {

    if (result.hasErrors()) {
      return ResponseEntity.badRequest().body(ValidationUtil.getErrorResponse(result));
    }

    try {
      Trip updatedTrip = tripService.updateTrip(tripId, payload);
      return ResponseEntity.ok(updatedTrip);
    } catch (ValidationException e) {
      return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage()));
    } catch (TripNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new ApiResponse(e.getMessage()));
    }
  }

  @GetMapping("/{tripId}/confirm")
  public ResponseEntity<?> confirmTrip(@PathVariable UUID tripId) {
    try {
      Trip trip = tripService.confirmTrip(tripId);
      return ResponseEntity.ok(trip);
    } catch (TripNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage()));
    }
  }

  @PostMapping("/{tripId}/invite")
  public ResponseEntity<?> inviteParticipant(@PathVariable UUID tripId, @RequestBody ParticipantPayloadDto payload) {
    try {
      TripCreateResponse response = tripService.inviteParticipantToTrip(tripId, payload);
      return ResponseEntity.ok(response);
    } catch (ValidationException e) {
      return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage()));
    }
  }

  @GetMapping("/{tripId}/participants")
  public ResponseEntity<?> getParticipantsByTripId(@PathVariable UUID tripId) {
    try {
      List<Participant> participants = tripService.getAllParticipantsFromEvent(tripId);
   
      return ResponseEntity.ok(participants);
    } catch (TripNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage()));
    }
  }

  @DeleteMapping("/{tripId}")
  public ResponseEntity<?> deleteTrip(@PathVariable UUID tripId) {
    try {
      ApiResponse response = tripService.deleteTrip(tripId);
      return ResponseEntity.ok(response);
    } catch (TripNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage()));
    }
  }
}