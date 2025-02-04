package school.faang.user_service.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.impl.UserServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapperImpl userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getUser_WhenUserExists_ReturnsUserDto() {
        long userId = 1L;
        User project = createTestUser();
        UserDto expectedUserDto = userMapper.toDto(project);

        when(userRepository.findById(userId)).thenReturn(Optional.of(project));

        ResponseEntity<UserDto> result = userService.getUser(userId);

        assertNotNull(result);
        assertEquals(expectedUserDto, result.getBody());

        verify(userRepository).findById(userId);
    }

    @Test
    void getUser_WhenUserDoesNotExist_ReturnsNotFoundResponse() {
        long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResponseEntity<UserDto> response = userService.getUser(userId);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        verify(userRepository).findById(userId);
        verifyNoInteractions(userMapper);
    }

    private User createTestUser() {
        return User.builder().id(1L).username("Test User").build();
    }

}