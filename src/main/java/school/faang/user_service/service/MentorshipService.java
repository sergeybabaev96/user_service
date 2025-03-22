package school.faang.user_service.service;

import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;

import java.util.List;

public interface MentorshipService {
    List<UserDto> getMentees(Long mentorId);

    List<UserDto> getMentors(Long menteeId);

    @Transactional
    void deleteMentee(Long menteeId, Long mentorId);

    @Transactional
    void deleteMentor(Long menteeId, Long mentorId);
}
