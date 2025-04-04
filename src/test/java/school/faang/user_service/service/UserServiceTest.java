package school.faang.user_service.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.UserNotFoundException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

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
