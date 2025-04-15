package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.service.mentorship.MentorshipRequestService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MentorshipRequestController {
    private final MentorshipRequestService mentorshipRequestService;

    @PostMapping("request")
    public RequestEntity<String> requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        try {
            mentorshipRequestService.requestMentorship(mentorshipRequestDto);
            return RequestEntity
        }catch ()
    }
}
