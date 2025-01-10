package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MentorshipController {

    private MentorshipService mentorshipService;

    public List<Long> getMentees(long mentorId) {
        return mentorshipService.getMentees(mentorId);
    }
}
