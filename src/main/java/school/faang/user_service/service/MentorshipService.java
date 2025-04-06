package school.faang.user_service.service;

import school.faang.user_service.dto.SuccessResponseDto;
import school.faang.user_service.dto.UserDto;

import java.util.List;

public interface MentorshipService {
    void createMentorship(long mentorId, long menteeId);

    boolean existsByMentorIdAndMenteeId(long mentorId, long menteeId);

    Long findMentorshipConnectionId(long mentorId, long menteeId);

    List<UserDto> getMentees(long mentorId);

    List<UserDto> getMentors(long menteeId);

    SuccessResponseDto deleteMentee(Long menteeId, Long mentorId);

    SuccessResponseDto deleteMentor(Long menteeId, Long mentorId);

//    TODO: метод не соответствует условию в задаче BJS2-66001: После деактивации профиля: если пользователь был
//     ментором других пользователей — необходимо остановить менторство. Для этого нужно написать соответствующий метод
//     в MentorshipService. Ментор должен пропасть из списка менторов своих менти. При этом все цели, которые ментор
//     поставил менти сохраняются, но больше не хранят mentorId — теперь они выглядят так,
//     как будто менти поставил их себе сам. Про ЦЕЛИ речь в условии, а не про связи в mentorship. Метод ниже
//     устанавливает менти самого себе ментором.
//    void deleteMentorShipByDeactivatedUser(Long mentorID);

    // TODO: удаление в задаче BJS2-66001 делать не просили
//    void deleteMenteeByDeactivatedUser(Long menteeId);
}
