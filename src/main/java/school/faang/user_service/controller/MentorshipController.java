package school.faang.user_service.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.SuccessResponseDto;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.MentorshipService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mentorship")
public class MentorshipController {

    private final MentorshipService mentorshipService;

    @GetMapping("/get-mentees")
    public List<UserDto> getMentees(Long mentorId) {
        return mentorshipService.getMentees(mentorId);
    }

    @GetMapping("/get-mentors")
    public List<UserDto> getMentors(Long menteeId) {
        return mentorshipService.getMentors(menteeId);
    }

    @DeleteMapping("/delete-mentee")
    public SuccessResponseDto deleteMentee(@PathVariable @Positive long menteeId,
                                           @PathVariable @Positive long mentorId) {
        return mentorshipService.deleteMentee(menteeId, mentorId);
    }

    @DeleteMapping("/delete-mentor")
    public SuccessResponseDto deleteMentor(@PathVariable @Positive long menteeId,
                                           @PathVariable @Positive long mentorId) {
        return mentorshipService.deleteMentor(menteeId, mentorId);
    }
}
