package school.faang.user_service.service;

import school.faang.user_service.dto.UserDto;

import java.util.List;

public interface MentorshipService {
    List<UserDto> getMentees(Long mentorId);

    List<UserDto> getMentors(Long menteeId);

    void deleteMentee(Long menteeId, Long mentorId);

    void deleteMentor(Long menteeId, Long mentorId);

    void deleteMentorShipByDeactivatedUser(Long mentorID);

    void deleteMenteeByDeactivatedUser(Long menteeId);
}
