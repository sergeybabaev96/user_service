package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.MenteeDto;
import school.faang.user_service.dto.MentorDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MentorshipMapper;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mentorship")
public class MentorshipController {
    private final MentorshipService mentorshipService;
    private final MentorshipMapper mentorshipMapper;

    @GetMapping("/{userId}/mentees")
    public List<MenteeDto> getMentees(@PathVariable Long userId) {
        List<User> mentees = mentorshipService.getMentees(userId);
        return mentorshipMapper.menteesToMenteesDtos(mentees);
    }

    @GetMapping("/{userId}/mentors")
    public List<MentorDto> getMentors(@PathVariable Long userId) {
        List<User> mentors = mentorshipService.getMentors(userId);
        return mentorshipMapper.mentorsToMentorsDtos(mentors);
    }

    @DeleteMapping("/{mentorId}/mentee/{menteeId}")
    public void deleteMentee(@PathVariable Long mentorId, @PathVariable Long menteeId) {
        mentorshipService.deleteMentee(mentorId, menteeId);
    }

    @DeleteMapping("/{menteeId}/mentor/{mentorId}")
    public void deleteMentor(@PathVariable Long menteeId, @PathVariable Long mentorId) {
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}
