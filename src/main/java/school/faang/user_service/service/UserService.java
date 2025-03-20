package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final EventService eventService;
    private final GoalService goalService;
    private final MentorshipService mentorshipService;
    private final UserMapper userMapper;

    public boolean doesUserExist(long userId) {
        return userRepository.existsById(userId);
    }

    public User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new DataRetrievalFailureException(
                        "User with id %d is not found".formatted(userId)));
    }

    @Transactional
    public UserDto deactivateUser(long userId) {
        eventService.deleteEventByUserId(userId);
        eventService.deleteParticipationFromEvent(userId);
        goalService.deleteUserFromGoals(userId);
        goalService.setNullInGoalsToMentor(userId);
        mentorshipService.deleteMentorShipByDeactivatedUser(userId);
        mentorshipService.deleteMenteeByDeactivatedUser(userId);
        User user = getUserById(userId);
        user.setActive(false);
        User deactivatedUser = userRepository.save(user);
        return userMapper.toDto(deactivatedUser);
    }
}
