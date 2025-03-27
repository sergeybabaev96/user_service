package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.MentorshipDto;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class MentorshipController {
    private final MentorshipService mentorshipService;

    @GetMapping("/mentees/{mentorId}")
    public List<MentorshipDto> getMentees(@PathVariable long mentorId) {
        return mentorshipService.getMentees(mentorId);
    }

    @GetMapping("/mentors/{userId}")
    public List<MentorshipDto> getMentors(@PathVariable long userId) {
        return mentorshipService.getMentors(userId);
    }

    @DeleteMapping("/mentor/{mentorId}/mentee/{menteeId}")
    public ResponseEntity<Void> deleteMentee(@PathVariable long mentorId, @PathVariable long menteeId) {
        mentorshipService.deleteMentee(menteeId, mentorId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/mentee/{menteeId}/mentor/{mentorId}")
    public ResponseEntity<Void> deleteMentor(@PathVariable long menteeId, @PathVariable long mentorId) {
        mentorshipService.deleteMentor(menteeId, mentorId);
        return ResponseEntity.noContent().build();
    }
}

