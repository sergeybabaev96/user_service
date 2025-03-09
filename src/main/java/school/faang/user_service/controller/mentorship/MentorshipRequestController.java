package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.service.MentorshipRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;

    public void requestMentorship(MentorshipRequestDto requestDto) {
        mentorshipRequestService.requestMentorship(requestDto);
    }

    public List<MentorshipRequest> getRequests(RequestFilterDto filter) {
        return mentorshipRequestService.getRequests(filter);
    }

    public void acceptRequest(Long requestId) {
        mentorshipRequestService.acceptRequest(requestId);
    }

    public void rejectRequest(Long requestId, MentorshipRequestDto requestDto) {
        mentorshipRequestService.rejectRequest(requestId, requestDto);
    }
}
