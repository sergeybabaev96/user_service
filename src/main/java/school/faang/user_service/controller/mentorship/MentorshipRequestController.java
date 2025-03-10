package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mentorship")
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping("/request")
    public MentorshipRequestDto requestMentorship(@RequestBody MentorshipRequestDto requestDto) {
        return mentorshipRequestService.requestMentorship(requestDto);
    }

    @GetMapping("/requests")
    public List<MentorshipRequestDto> getRequests(@ModelAttribute RequestFilterDto filter) {
        return mentorshipRequestService.getRequests(filter);
    }

    @PostMapping("/accept/{requestId}")
    public MentorshipRequestDto acceptRequest(@PathVariable Long requestId) {
        return mentorshipRequestService.acceptRequest(requestId);
    }

    @PostMapping("/reject/{requestId}")
    public MentorshipRequestDto rejectRequest(@PathVariable Long requestId, @RequestBody RejectionDto rejection) {
        return mentorshipRequestService.rejectRequest(requestId, rejection);
    }
}
