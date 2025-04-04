package school.faang.user_service.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.goal.GoalService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;
    @Mock
    private GoalMapper goalMapper;
    @Mock
    private GoalService goalService;
    @Mock
    private EventService eventService;
    @Mock
    private MentorshipService mentorshipService;

    @InjectMocks
    UserService userService;

    @Test
    @DisplayName("Negative: error when user not found")
    void testFindUserNegativeNoUser() {
        long idForSearch = 1L;
        when(userRepository.findById(idForSearch)).thenReturn(Optional.empty());

        var exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.findUserById(idForSearch)
        );

        assertEquals(exception.getMessage(), String.format("User with id = %d doesn't exist", idForSearch));
    }

    @Test
    @DisplayName("Positive: successful find user by id")
    void testFindUserSuccess() {
        User user = createUser(1L);
        UserDto userDto = createUserDto(1L);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);
        UserDto result = userService.findUserById(user.getId());

        assertEquals(userDto, result);
        assertEquals(user.getId(), result.id());
    }

    @Test
    @DisplayName("Negative: no users found by ids")
    void testGetUsersByIdsNegativeNoUsers() {
        List<Long> ids = List.of(1L, 2L, 3L);

        when(userRepository.findAllById(ids)).thenReturn(Collections.emptyList());
        List<UserDto> users = userService.getUsersByIds(ids);

        assertEquals(0, users.size());
    }

    @Test
    @DisplayName("Positive: successful find users by ids")
    void testGetUsersByIdsSuccess() {
        List<Long> ids = List.of(1L, 2L, 3L);
        List<User> users = createUsers(ids);
        List<UserDto> userDtos = createUserDtos(users);

        when(userRepository.findAllById(ids)).thenReturn(users);
        when(userMapper.toDto(any(User.class))).thenAnswer(input -> {
            User user = input.getArgument(0);
            return createUserDto(user.getId());
        });
        List<UserDto> result = userService.getUsersByIds(ids);

        assertEquals(userDtos, result);
    }

    @Test
    @DisplayName("Positive: successful activate user")
    void shouldActivateUserIfUpdatedRecently() {
        Long id = 1L;
        User user = new User();
        user.setId(id);
        user.setActive(false);
        user.setUpdatedAt(LocalDateTime.now().minusMonths(1));

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserDto dto = UserDto.builder().id(id).username("gor").email("gor@x.com").active(true).build();
        when(userMapper.toDto(user)).thenReturn(dto);

        UserDto result = userService.activateUser(id);

        verify(userRepository).save(user);
        assertTrue(result.active());
    }

    @Test
    @DisplayName("Positive: successful deactivate user")
    void shouldDeactivateUser() {
        Long id = 1L;
        User user = new User();
        user.setId(id);
        user.setActive(true);
        user.setGoals(List.of());

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(
                UserDto.builder().id(id).username("deact").email("d@x.com").active(false).build()
        );
        when(eventService.getParticipatedEvents(id)).thenReturn(List.of());

        UserDto result = userService.deactivateUser(id);

        verify(goalService).deleteAllByIds(any());
        verify(goalService).removeUserFromGoals(any(), eq(id));
        verify(eventService).deleteAllByIds(any());
        verify(eventService).removeUserFromEvents(any(), eq(id));
        verify(mentorshipService).deleteMentorship(id);
        assertFalse(result.active());
    }

    @Test
    @DisplayName("Positive: successful stop user goals")
    void shouldStopUserGoalsCorrectly() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        Goal goal1 = new Goal();
        Goal goal2 = new Goal();
        user.setGoals(List.of(goal1, goal2));

        GoalDto deletableGoal = GoalDto.builder()
                .id(101L)
                .title("Deletable Goal")
                .userIds(List.of(userId))
                .build();

        GoalDto removableGoal = GoalDto.builder()
                .id(102L)
                .title("Shared Goal")
                .userIds(List.of(userId, 2L))
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(goalMapper.goalToGoalDto(goal1)).thenReturn(deletableGoal);
        when(goalMapper.goalToGoalDto(goal2)).thenReturn(removableGoal);
        when(eventService.getParticipatedEvents(userId)).thenReturn(List.of());

        when(userMapper.toDto(any())).thenReturn(UserDto.builder()
                .id(userId)
                .username("test")
                .email("t@t.com")
                .active(false)
                .build());

        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        userService.deactivateUser(userId);

        verify(goalService).deleteAllByIds(List.of(101L));
        verify(goalService).removeUserFromGoals(List.of(102L), userId);
    }



    private User createUser(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

    private List<User> createUsers(List<Long> ids) {
        return ids.stream()
                .map(this::createUser)
                .toList();
    }

    private UserDto createUserDto(Long id) {
        return new UserDto(id, "test", "test", true);
    }

    private List<UserDto> createUserDtos(List<User> users) {
        return users.stream()
                .map(user -> createUserDto(user.getId()))
                .toList();
    }
}
