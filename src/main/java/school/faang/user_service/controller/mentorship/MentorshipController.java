package school.faang.user_service.controller.mentorship;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MentorshipController {

    private final MentorshipService mentorshipService;

    @GetMapping("/mentor/{mentorId}/mentees")
    public List<UserDto> getMentees(@Valid @PathVariable @Positive @Min(1) long mentorId) {
        return mentorshipService.getMentees(mentorId);
    }

    @GetMapping("/mentee/{menteeId}/mentors")
    public List<UserDto> getMentors(@PathVariable @Positive @Min(1) long menteeId) {
        return mentorshipService.getMentors(menteeId);
    }

    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @DeleteMapping("/mentee/{menteeId}/mentor/{mentorId}")
    public void deleteMenteeAndMentor(@PathVariable @Positive @Min(1) long menteeId,
                                      @PathVariable @Positive @Min(1) long mentorId) {
        mentorshipService.deleteMenteeAndMentor(menteeId, mentorId);
    }
}