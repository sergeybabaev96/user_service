package school.faang.user_service.controller.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class GetUserControllerTest {

    private static final List<User> TESTS_USERS = getTestsUsers();
    private static final List<UserDto> TESTS_DTOS = getTestsUserDtos();

    @Mock
    private UserService userService;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private GetUserController getUserController;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("getUser(). Positive")
    void getUser() {
        Mockito.when(userService.getUser(any(Long.class))).thenReturn(Optional.of(TESTS_USERS.get(0)));
        Mockito.when(userMapper.toDto(any(User.class))).thenReturn(TESTS_DTOS.get(0));

        assertEquals(TESTS_DTOS.get(0), getUserController.getUser(0));
        Mockito.verify(userMapper, Mockito.times(1)).toDto(any());
    }

    @Test
    @DisplayName("getUser(). Negative. Id is negative")
    void getUserWithIncorrectId() {

        assertThrows(RuntimeException.class, () -> getUserController.getUser(-1));
        Mockito.verify(userService, Mockito.never()).getUser(any());
        Mockito.verify(userMapper, Mockito.never()).toDto(any());
    }

    @Test
    @DisplayName("getUser(). Negative. User is not found")
    void getUserWithException() {
        Mockito.when(userService.getUser(any(Long.class))).thenReturn(Optional.ofNullable(null));

        assertThrows(RuntimeException.class, () -> getUserController.getUser(1));
        Mockito.verify(userService, Mockito.atLeastOnce()).getUser(any());
        Mockito.verify(userMapper, Mockito.never()).toDto(any());
    }

    @Test
    @DisplayName("getUser(). Positive")
    void getUsers() {
        List<Long> ids = List.of(1L, 2L ,3L);
        Mockito.when(userService.getUsers(any())).thenReturn(TESTS_USERS);
        Mockito.when(userMapper.toDto(any(User.class))).thenReturn(TESTS_DTOS.get(0));

        assertEquals(TESTS_USERS.size(), getUserController.getUsers(ids).size());
        Mockito.verify(userService, Mockito.times(1)).getUsers(any());
        Mockito.verify(userMapper, Mockito.times(TESTS_USERS.size())).toDto(any());
    }

    @ParameterizedTest
    @DisplayName("getUser(). Negative. List of id is incorrect or empty")
    @MethodSource("getIncorrectIds")
    void getUsersWithException(List<Long> incorrectIds) {

        assertThrows(RuntimeException.class, () -> getUserController.getUsers(incorrectIds));
        Mockito.verify(userService, Mockito.never()).getUsers(any());
        Mockito.verify(userMapper, Mockito.never()).toDto(any());
    }

    private static Stream<Arguments> getIncorrectIds() {
        return Stream.of(
                Arguments.of(List.of()),
                Arguments.of(List.of(-1L, -2L, -3L)));
    }

    private static List<User> getTestsUsers() {
        return List.of(
                User.builder().id(1L).username("name1").email("1@1.ru").build(),
                User.builder().id(2L).username("name2").email("2@1.ru").build(),
                User.builder().id(3L).username("name3").email("3@1.ru").build());
    }

    private static List<UserDto> getTestsUserDtos() {
        return List.of(
                new UserDto(),
                new UserDto(),
                new UserDto());
    }
}