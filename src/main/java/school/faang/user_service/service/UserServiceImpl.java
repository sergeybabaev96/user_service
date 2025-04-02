package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.exception.UsernameNotFoundException;
import school.faang.user_service.exception.UsernameNotUniqueException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    //    private final EventService eventService;
//    private final GoalService goalService;
//    private final MentorshipService mentorshipService;
    private final UserMapper userMapper;

    @Override
    public User getReferenceById(long userId) {
        return userRepository.getReferenceById(userId);
    }

    @Override
    public long findUniqueIdByUsername(String username) {
        List<Long> userIds = userRepository.findIdByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Username '%s' not found".formatted(username)));
        long userId;
        if (userIds.size() == 1) {
            userId = userIds.get(0);
        } else {
            throw new UsernameNotUniqueException("Username '%s' must be unique".formatted(username));
        }
        return userId;
    }

    @Override
    public User findUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        "User with id %d not found".formatted(userId)));
    }

    @Override
    public void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            log.error("User with ID {} does not exist", userId);
            throw new UserNotFoundException("User does not exist.");
        }
    }

    @Override
    public boolean existsById(long userId) {
        return userRepository.existsById(userId);
    }

    // TODO: задача BJS2-66001 сделана неверно

//    @Override
//    @Transactional
//    public UserDto deactivateUser(long userId) {
//        eventService.deleteEventByUserId(userId);
//        eventService.deleteParticipationFromEvent(userId);
//        goalService.deleteUserFromGoals(userId);
//        mentorshipService.deleteMentorShipByDeactivatedUser(userId);
//        mentorshipService.deleteMenteeByDeactivatedUser(userId);
//        User user = findUserById(userId);
//        user.setActive(false);
//        User deactivatedUser = userRepository.save(user);
//        return userMapper.toDto(deactivatedUser);
//    }

    @Override
    public UserDto getUser(long userId) {
        var user = findUserById(userId);
        return userMapper.toDto(user);
    }

    @Override
    public List<UserDto> getUsersByIds(List<Long> ids) {
        var users = userRepository.findAllById(ids);
        return userMapper.toDtoList(users);
    }
}
