package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.MentorshipDto;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

@RestController
@RequestMapping("/mentorship")
@RequiredArgsConstructor
public class MentorshipController {
    private final MentorshipService mentorshipService;

    @GetMapping("/mentors/{mentorId}/mentees")
    @ResponseStatus(HttpStatus.OK)
    public List<MentorshipDto> getMentees(@PathVariable("mentorId") long mentorId) {
        return mentorshipService.getMentees(mentorId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/mentees/{menteeId}/mentors")
    public List<MentorshipDto> getMentors(@PathVariable("menteeId") long menteeId) {
        return mentorshipService.getMentors(menteeId);
    }

    @DeleteMapping("/mentors/{mentorId}/mentees/{menteeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMentee(@PathVariable("menteeId") long menteeId, @PathVariable("mentorId") long mentorId) {
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    @DeleteMapping("/mentees/{menteeId}/mentors/{mentorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMentor(@PathVariable("menteeId") long menteeId, @PathVariable("mentorId") long mentorId) {
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}
