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


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Validated
public class MentorshipController {

    private final MentorshipService mentorshipService;

    @GetMapping("/mentor/{mentorId}/mentees")
    public List<UserDto> getMentees(@PathVariable long mentorId) {
        validateDId(mentorId);
        return mentorshipService.getMentees(mentorId);
    }

    @GetMapping("/mentee/{menteeId}/mentors")
    public List<UserDto> getMentors(@PathVariable  long menteeId) {
        validateDId(menteeId);
        return mentorshipService.getMentors(menteeId);
    }

    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @DeleteMapping("/mentee/{menteeId}/mentor/{mentorId}")
    public void deleteMenteeAndMentor(@Valid @PathVariable @Positive @Min(1) long menteeId,
                                      @Valid @PathVariable @Positive @Min(1) long mentorId) {
        mentorshipService.deleteMenteeAndMentor(menteeId, mentorId);
    }

    private void validateDId(long id){
        if(id < 1){
            throw new IllegalArgumentException("ID must be greater than or equal to 1");
        }
    }
}