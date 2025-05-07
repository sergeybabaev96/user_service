package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.MenteeDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.MenteeMapper;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mentorship")
public class MentorshipController {
    private final MentorshipService mentorshipService;
    private final MenteeMapper menteeMapper;

    @GetMapping("/{userId}/mentees")
    public List<MenteeDto> getMentees(@PathVariable Long userId) {
        List<User> mentees = mentorshipService.getMentees(userId);
        return menteeMapper.menteesToMenteesDtos(mentees);
    }
}
