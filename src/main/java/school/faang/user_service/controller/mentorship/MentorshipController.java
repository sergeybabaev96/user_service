package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MentorshipController {

    private final MentorshipService mentorshipService;

    @GetMapping("/mentor/{mentorId}/mentees")
    public List<UserDto> getMentees(@PathVariable long mentorId) {
        return mentorshipService.getMentees(mentorId);
    }

    @GetMapping("/mentee/{menteeId}/mentors")
    public List<UserDto> getMentors(@PathVariable long menteeId) {
        return mentorshipService.getMentors(menteeId);
    }

    @DeleteMapping("/mentee/{menteeId}/mentor/{mentorId}")
    public ResponseEntity<Void> deleteMenteeAndMentor(@PathVariable long menteeId, @PathVariable long mentorId) {
        mentorshipService.deleteMenteeAndMentor(menteeId, mentorId);
        return ResponseEntity.noContent().build();
    }
}