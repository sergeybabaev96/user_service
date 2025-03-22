package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.validator.user.UserValidator;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    public static final String USER_NOT_FOUND = "User not found";
    private static final Long USER_ID = 1L;

    private User user;
    private UserDto userDto;
    private List<User> users;
    private List<UserDto> userDtos;
    private List<Long> userIds;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserValidator userValidator;
    @Spy
    private UserMapperImpl userMapper;
    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        user = User.builder().id(USER_ID).username("Join").build();
        userDto = UserDto.builder().id(USER_ID).username("Join").build();
        users = List.of(user, User.builder().id(2L).username("Bob").build());
        userDtos = List.of(userDto, UserDto.builder().id(2L).username("Bob").build());
        userIds = List.of(1L, 2L);
    }

    @Test
    public void testGetUserNotFound() {
        doThrow(new EntityNotFoundException(USER_NOT_FOUND))
                .when(userValidator).checkUserExistsById(USER_ID);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userService.getUser(USER_ID));
        verify(userValidator, times(1)).checkUserExistsById(USER_ID);
        assertEquals(USER_NOT_FOUND, exception.getMessage());
        verify(userRepository, never()).findById(USER_ID);
        verify(userMapper, never()).toUser(any());
    }

    @Test
    public void testGetUserSuccessful() {
        doNothing().when(userValidator).checkUserExistsById(USER_ID);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        UserDto resultDto = userService.getUser(USER_ID);

        verify(userValidator, times(1)).checkUserExistsById(USER_ID);
        verify(userRepository, times(1)).findById(USER_ID);
        verify(userMapper, times(1)).toUser(user);
        assertNotNull(resultDto);
        assertEquals(userDto.getId(), resultDto.getId());
        assertEquals(userDto.getUsername(), resultDto.getUsername());
    }

    @Test
    public void testGetUsersByIdsUserNotFound() {
        Long id = 2L;
        doNothing().when(userValidator).checkUserExistsById(USER_ID);
        doThrow(new EntityNotFoundException(USER_NOT_FOUND))
                .when(userValidator).checkUserExistsById(id);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userService.getUsersByIds(userIds));
        assertEquals(USER_NOT_FOUND, exception.getMessage());
        verify(userValidator, times(1)).checkUserExistsById(USER_ID);
        verify(userValidator, times(1)).checkUserExistsById(id);
        verify(userRepository, never()).findAllById(userIds);
        verify(userMapper, never()).toListUserDto(any());
    }

    @Test
    public void testGetUsersByIdsSuccessful() {
        doNothing().when(userValidator).checkUserExistsById(anyLong());
        when(userRepository.findAllById(userIds)).thenReturn(users);

        List<UserDto> resultDtos = userService.getUsersByIds(userIds);

        assertNotNull(resultDtos);
        assertEquals(userDtos.size(), resultDtos.size());
        assertEquals(userDtos.get(0).getId(), resultDtos.get(0).getId());
        assertEquals(userDtos.get(0).getUsername(), resultDtos.get(0).getUsername());
        assertEquals(userDtos.get(1).getId(), resultDtos.get(1).getId());
        assertEquals(userDtos.get(1).getUsername(), resultDtos.get(1).getUsername());
        verify(userValidator, times(2)).checkUserExistsById(anyLong());
        verify(userRepository, times(1)).findAllById(userIds);
        verify(userMapper, times(1)).toListUserDto(users);
    }
}
