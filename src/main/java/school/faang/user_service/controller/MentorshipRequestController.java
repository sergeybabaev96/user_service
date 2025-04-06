package school.faang.user_service.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.MentorshipRejectionDto;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.MentorshipRequestFilterDto;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/mentorship-requests")
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping("/create")
    public MentorshipRequestDto requestMentorship(@Valid @RequestBody MentorshipRequestDto mentorshipRequestDto) {
        return mentorshipRequestService.requestMentorship(mentorshipRequestDto);
    }

    @GetMapping("/filter")
    public List<MentorshipRequestDto> getRequests(MentorshipRequestFilterDto filter) {
        return mentorshipRequestService.getRequests(filter);
    }

    @PutMapping("/{id}/accept")
    public MentorshipRequestDto acceptRequest(@PathVariable @Positive long id) {
        return mentorshipRequestService.acceptRequest(id);
    }

    @PutMapping("/{id}/reject")
    public MentorshipRequestDto rejectRequest(
            @PathVariable @Positive long id,
            @Valid @RequestBody MentorshipRejectionDto rejection) {
        return mentorshipRequestService.rejectRequest(id, rejection);
    }
}
