package com.rocketseat.planner.participants;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rocketseat.planner.common.ValidationUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/participant")
public class ParticipantController {
  @Autowired
  private ParticipantRepository participantRepository;

  @PostMapping("/{id}/confirm")
  public ResponseEntity<?> confirmParticipant(@PathVariable UUID id,
      @Valid @RequestBody ParticipantPayloadDto payload, BindingResult result) {
    if (result.hasErrors()) {
      return ResponseEntity.badRequest().body(ValidationUtil.getErrorResponse(result));
    }

    Optional<Participant> participant = this.participantRepository.findById(id);

    if (participant.isPresent()) {
      Participant rawParticipant = participant.get();
      rawParticipant.setConfirmed(true);
      rawParticipant.setName(payload.name());

      this.participantRepository.save(rawParticipant);

      return ResponseEntity.ok(new ParticipantConfirmResponse(rawParticipant.getId()));
    }
    return ResponseEntity.notFound().build();

  }
}
