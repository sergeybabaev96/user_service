package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.UserDto;
import jakarta.persistence.EntityNotFoundException;
import school.faang.user_service.entity.User;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.filter.user.UserPageFilter;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final List<UserFilter> userFilters;
    private final GoalService goalService;
    private final EventService eventService;
    private final UserMapper userMapper;
    private final MentorshipService mentorshipService;

    public UserDto findUserById(Long id) {
        return userMapper.toDto(userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id not found")));
    }

    public List<UserDto> getPremiumUsers(UserFilterDto userFilterDto) {
        var users = userRepository.findPremiumUsers();

        return filterUsers(userFilterDto, users)
                .map(userMapper::toDto)
                .toList();
    }

    private Stream<User> filterUsers(UserFilterDto filters, Stream<User> userStream) {
        for (UserFilter userFilter : userFilters) {
            if (userFilter.isApplicable(filters)) {
                userStream = userFilter.apply(userStream, filters);
            }
        }
        UserPageFilter userPageFilter = new UserPageFilter();
        if (userPageFilter.isApplicable(filters)) {
            userStream = userPageFilter.apply(userStream, filters);
        }
        return userStream;
    }

    @Transactional
    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));

        goalService.stopGoalsByUser(id);
        eventService.cancelEventsByUser(id);
        mentorshipService.stopMentorship(user);

        user.setActive(false);
        userRepository.save(user);
    }

    public List<UserDto> findUsersByIds(List<Long> ids) {
        return userRepository.findAllById(ids)
                .stream()
                .map(userMapper::toDto)
                .toList();
    }
}
