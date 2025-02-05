package school.faang.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.MentorshipResponseDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.MentorshipRequestFilterDto;
import school.faang.user_service.service.impl.MentorshipRequestServiceImpl;

import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mentorship/requests")
public class MentorshipRequestController {
    private final MentorshipRequestServiceImpl service;

    @PostMapping
    @Operation(
            summary = "Method for creating a new mentoring request",
            description = """
                    The method verifies that:
                                        
                        - the user who requests mentoring and the user who is being requested exist in the database;
                        - takes into account that a request for mentoring can be made only once every 3 months;
                        - the user cannot send a request to himself;
                    In case of successful verification, the mentoring request is saved in the database.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mentorship request was successfully saved to the DB"),
            @ApiResponse(responseCode = "400", description = "The request parameters were not validated")
    })
    public MentorshipResponseDto requestMentorship(@RequestBody MentorshipRequestDto mentorshipRequestDto) {
        log.info("#requestMentorship: mentorship request has been received from user with id: {}",
                mentorshipRequestDto.requester().getUserId());
        if (Objects.isNull(mentorshipRequestDto.description()) || mentorshipRequestDto.description().isBlank()) {
            throw new IllegalArgumentException(String.format("""
                    Request from user with id: %d does not contain a description.
                    The description cannot be missing or empty.
                    """, mentorshipRequestDto.requester().getUserId()));
        }
        return service.requestMentorship(mentorshipRequestDto);
    }

    @GetMapping
    public List<MentorshipResponseDto> getRequests(MentorshipRequestFilterDto filter) {
        log.info("#getRequests: request has been received to receive all mentoring requests that match the filters");
        return service.getRequests(filter);
    }

    @PutMapping("/{id}/accept")
    public void acceptRequest(@PathVariable("id") long requestId) {
        log.info("#acceptRequest: request has been received to accept a mentoring request from user with id: {}",
                requestId);
        service.acceptRequest(requestId);
    }

    @PutMapping("/{id}/reject")
    public void rejectRequest(@PathVariable("id") long requestId, @RequestBody RejectionDto rejection) {
        log.info("#rejectRequest: request has been received to reject a mentoring request from user with id: {}",
                requestId);
        service.rejectRequest(requestId, rejection);
    }
}
