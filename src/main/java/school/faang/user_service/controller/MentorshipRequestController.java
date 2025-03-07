package school.faang.user_service.controller;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.exception.MentorshipAlreadyExistsException;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.List;

@RestController
@RequestMapping("/mentorship-request")
@RequiredArgsConstructor
public class MentorshipRequestController {

    private static final int DESCRIPTION_MAX_LENGTH = 4096;
    private static final int DESCRIPTION_MIN_LENGTH = 100;
    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping("/requests")
    public ResponseEntity<String> requestMentorship(@RequestBody MentorshipRequestDto mentorshipRequestDto) {
        try {
            validateMentorshipRequest(mentorshipRequestDto);
            mentorshipRequestService.requestMentorship(mentorshipRequestDto);
            return ResponseEntity.ok("Request created successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // 400
        }
    }

    @GetMapping("/requests")
    public ResponseEntity<List<MentorshipRequestDto>> getRequests(@RequestParam RequestFilterDto filter) {
        try {
            List<MentorshipRequestDto> requests = mentorshipRequestService.getRequests(filter);
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // 500
        }
    }

    @PostMapping("/requests/{id}/accept")
    public ResponseEntity<String> acceptRequest(@PathVariable long id) {
        try {
            mentorshipRequestService.acceptRequest(id);
            return ResponseEntity.ok("Request accepted");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404
        } catch (MentorshipAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage()); // 409
        }
    }

    @PostMapping("/requests/{id}/reject")
    public ResponseEntity<String> rejectRequest(@PathVariable long id, @RequestBody RejectionDto rejection) {
        try {
            mentorshipRequestService.rejectRequest(id, rejection);
            return ResponseEntity.ok("Request rejected");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404
        }
    }

    private void validateMentorshipRequest(MentorshipRequestDto mentorshipRequestDto) {
        String desc = mentorshipRequestDto.getDescription();
        if (desc.isBlank()) {
            throw new IllegalArgumentException("Description is empty");
        }
        if (desc.length() > DESCRIPTION_MAX_LENGTH) {
            throw new IllegalArgumentException("Description has more than 4096 characters");
        }
        if (desc.length() < DESCRIPTION_MIN_LENGTH) {
            throw new IllegalArgumentException("Tell more about your reasons for the request");
        }
    }
}