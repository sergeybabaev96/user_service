package school.faang.user_service.service.mentorship;

import school.faang.user_service.dto.UserDto;

import java.util.List;

public interface MentorshipService {

    List<UserDto> getMentees(long userId);

    List<UserDto> getMentors(long userId);

    void deleteMentee(long menteeId, long mentorId);

    void deleteMentor(long menteeId, long mentorId);

    void deactivateMentorship(long userId);

}