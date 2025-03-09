package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.MentorshipDto;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MentorshipController {
    private final MentorshipService mentorshipService;

    public List<MentorshipDto> getMentees(long mentorId) {
        return mentorshipService.getMentees(mentorId);
    }

    public List<MentorshipDto> getMentors(long userId) {
        return mentorshipService.getMentors(userId);
    }

    public void deleteMentee(long mentorId, long menteeId) {
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}

