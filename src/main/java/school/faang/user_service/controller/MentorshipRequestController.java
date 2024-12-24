package school.faang.user_service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.mentorship_request.MentorshipRequestCreateDto;
import school.faang.user_service.dto.mentorship_request.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship_request.MentorshipRequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/mentorship-requests")
public class MentorshipRequestController {
    private final MentorshipRequestService requestService;

    @PostMapping
    public ResponseEntity<MentorshipRequestDto> requestMentorship(@Valid @RequestBody MentorshipRequestCreateDto dto) {
        log.info("Requesting mentorship from userId #{} to userId #{}.", dto.getRequesterId(), dto.getReceiverId());
        return ResponseEntity.status(HttpStatus.CREATED).body(requestService.requestMentorship(dto));
    }

    @GetMapping("/filtered")
    public ResponseEntity<List<MentorshipRequestDto>> getRequests(
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Long requesterId,
            @RequestParam(required = false) Long receiverId,
            @RequestParam(required = false) RequestStatus status
    ) {
        MentorshipRequestFilterDto filters = MentorshipRequestFilterDto.builder()
                .descriptionPattern(description)
                .requesterId(requesterId)
                .receiverId(receiverId)
                .status(status)
                .build();
        log.info("Getting all requests by filters: {}.", filters);

        return ResponseEntity.ok(requestService.getRequests(filters));
    }

    @PutMapping("/accepts/{id}")
    public ResponseEntity<MentorshipRequestDto> acceptRequest(@PathVariable @NotNull @Positive Long id) {
        log.info("Accepting request with id #{}", id);
        return ResponseEntity.ok(requestService.acceptRequest(id));
    }

    @PatchMapping("/rejects/{id}")
    public ResponseEntity<MentorshipRequestDto> rejectRequest(@PathVariable @NotNull @Positive Long id,
                                                              @Valid @RequestBody RejectionDto rejectionDto) {
        log.info("Rejecting request with id #{} and reason '{}'", id, rejectionDto.getReason());
        return ResponseEntity.ok(requestService.rejectRequest(id, rejectionDto));
    }
}
