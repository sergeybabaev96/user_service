package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.MentorshipResponseDto;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MentorshipController {
    private final MentorshipService mentorshipService;

    public List<MentorshipResponseDto> getMentees(Long userId) {
        return mentorshipService.getMentees(userId);
    }

    public List<MentorshipResponseDto> getMentors(Long userId) {
        return mentorshipService.getMentors(userId);
    }

    public void deleteMentee(long mentorId, long menteeId) {
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    public void deleteMentor(long menteeId, long mentorId) {
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}
