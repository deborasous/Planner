package com.rocketseat.planner.participants;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rocketseat.planner.common.ApiResponse;
import com.rocketseat.planner.common.ValidationUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/participant")
public class ParticipantController {
  @Autowired
  private ParticipantRepository participantRepository;

  @Autowired
  private ParticipantService participantService;

  @PostMapping("/{participantId}/confirm")
  public ResponseEntity<?> confirmParticipant(@PathVariable UUID participantId,
      @Valid @RequestBody ParticipantPayloadDto payload, BindingResult result) {
    if (result.hasErrors()) {
      return ResponseEntity.badRequest().body(ValidationUtil.getErrorResponse(result));
    }

    try {
      ParticipantCreateResponse response = participantService.confirmParticipant(participantId, payload);
      return ResponseEntity.ok(response);
    } catch (ParticipantNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage()));
    }
  }
}
