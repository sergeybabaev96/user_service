package school.faang.user_service.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.dto.user.UserReadDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.stream.Stream;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final Long USER_ID = 2L;
    private static final Long UNREAL_USER_ID = 99L;
    private static final int INACTIVATION_PERIOD_DAYS = 91;

    @Mock
    private MentorshipService mentorshipService;

    @Spy
    private UserMapperImpl userMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private List<UserFilter> users;

    @InjectMocks
    private UserService userService;

    private User activeUsers;
    private User inactiveUser;
    private User activeUser;
    private UserReadDto userDto;

    @BeforeEach
    public void setUp() {

        activeUsers = new User();
        activeUsers.setActive(true);
        activeUsers.setUpdatedAt(LocalDateTime.now());

        inactiveUser = new User();
        inactiveUser.setActive(false);
        inactiveUser.setUpdatedAt(LocalDateTime.now().minusDays(INACTIVATION_PERIOD_DAYS));

        activeUser = User.builder()
                .id(1L)
                .active(true)
                .updatedAt(LocalDateTime.now())
                .ownedEvents(new ArrayList<>())
                .goals(new ArrayList<>())
                .build();

        userDto = UserReadDto.builder().id(USER_ID).build();
    }

    @Test
    void shouldSuccessWhenUserExists() {
        when(userRepository.existsById(anyLong())).thenReturn(true);

        userService.isUserExists(anyLong());
        verify(userRepository).existsById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void shouldThrowBusinessExceptionWhenUserNotExists() {
        long nonExistingUserId = 123L;
        when(userRepository.existsById(nonExistingUserId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> userService.isUserExists(nonExistingUserId),
                "Пользователя с id " + nonExistingUserId + " не существует");
    }

    @Test
    void shouldSuccessSaveUser() {
        User user = new User();
        userService.saveUser(user);

        verify(userRepository).save(user);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testRemoveMenteeAndGoals() {
        userService.removeMenteeAndGoals(USER_ID);
        verify(mentorshipService, times(1)).removeMenteeGoals(USER_ID);
        verify(mentorshipService, times(1)).removeMenteeFromUser(USER_ID);
    }

    @Test
    void testNotDeleteActiveUsers() {
        when(userRepository.findAll()).thenReturn(List.of(activeUsers));

        userService.deleteInactiveUsers();

        verify(userRepository, never()).delete(activeUsers);
    }

    @Test
    void testDeleteUsersAfterDeactivationPeriod() {

        when(userRepository.findAll()).thenReturn(List.of(inactiveUser));

        userService.deleteInactiveUsers();

        verify(userRepository, times(1)).delete(inactiveUser);
    }

    @Test
    void testDeactivate() {

        when(userRepository.findById(activeUser.getId())).thenReturn(Optional.of(activeUser));
        when(userRepository.existsById(activeUser.getId())).thenReturn(true);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toDto(any(User.class))).thenReturn(userDto);

        UserReadDto result = userService.deactivate(activeUser.getId());

        assertNotNull(result);
        assertFalse(result.isActive());
        verify(userRepository, times(3)).findById(activeUser.getId());

    }

    @Test
    void testDeactivate_UserNotFound() {
        when(userRepository.findById(UNREAL_USER_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.deactivate(UNREAL_USER_ID));
        verify(userRepository).findById(UNREAL_USER_ID);
    }

    @Test
    void testGetPremiumUsers() {
        User user = new User();
        UserReadDto userDto = UserReadDto.builder().id(1L).username("John").email("john@example.com").build();
        UserFilterDto userFilterDto = new UserFilterDto();
        when(userRepository.findPremiumUsers()).thenReturn((Stream.of(user)));
        when(userMapper.toDto(user)).thenReturn(userDto);

        List<UserReadDto> result = userService.getPremiumUsers(userFilterDto);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(userDto, result.get(0));
        verify(userRepository, times(1)).findPremiumUsers();
    }

    @Test
    void shouldGetUser() {
        UserReadDto userReadDto = UserReadDto.builder().id(1L).build();
        User user = User.builder().id(1L).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Assertions.assertEquals(userService.getUser(1L), userReadDto);
        verify(userRepository).findById(1L);
    }

    @Test
    void shouldGetUserUsersByIds() {
        User userFinal = User.builder().id(1L).build();
        List<User> users = new ArrayList<>(List.of(userFinal));
        List<Long> ids = new ArrayList<>(List.of(1L));
        when(userRepository.findAllById(ids)).thenReturn(users);

        Assertions.assertEquals(users.stream()
                        .map(userResult -> userMapper.toDto(userResult)).toList(), userService.getUsersByIds(ids));
        verify(userRepository).findAllById(ids);
    }
}