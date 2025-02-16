package school.faang.user_service.controller.mentorship;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.mentorship.MentorshipRequestMapper;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mentorship")
@RestController
@Validated
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;
    private final MentorshipRequestMapper requestMapper;

    @Operation(
            summary = "Request for mentorship",
            description = "Sent request for mentorship, use description for reason, mentor and mentee ids required")
    @PostMapping("/request")
    public MentorshipRequestDto requestMentorship(@RequestBody MentorshipRequestDto requestDto) {
        if (requestDto.getRequesterId() == requestDto.getReceiverId()) {
            throw new DataValidationException("User can't request mentoring from yourself");
        }
        MentorshipRequest request = mentorshipRequestService.requestMentorship(requestMapper.toEntity(requestDto));
        return requestMapper.toDto(request);
    }

    @Operation(
            summary = "Get all mentorship requests by filter",
            description = "Filter requests by description, mentor, mentee or status")
    @PostMapping("/getRequests")
    public List<MentorshipRequestDto> getRequests(@RequestBody RequestFilterDto filter) {
        return requestMapper.toEntityList(mentorshipRequestService.getRequests(filter));
    }

    @Operation(
            summary = "Accept mentorship request",
            description = "Accept request, if not in mentor's list")
    @PutMapping("/{id}/accept")
    public void acceptRequest(
            @PathVariable("id") @Min(value = 1, message = "Mentorship id must be greater than 0")
            long requestId) {
        mentorshipRequestService.acceptRequest(requestId);
    }

    @Operation(
            summary = "Reject mentorship request",
            description = "Reject mentorship request by reason")
    @PutMapping("/{id}/reject")
    public MentorshipRequestDto rejectRequest(
            @PathVariable("id") @Min(value = 1, message = "ID must be greater than 0")
            long requestId, @RequestBody RejectionDto rejection) {
        return requestMapper.toDto(mentorshipRequestService.rejectRequest(requestId, rejection));
    }
}
