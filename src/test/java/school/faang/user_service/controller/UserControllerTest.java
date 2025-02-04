package school.faang.user_service.controller;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService projectService;

    @InjectMocks
    private UserController projectController;

    @Test
    void getUser_WhenUserExists_ReturnsUserDto() {
        long userId = 1L;
        UserDto expectedUserDto = UserDto.builder().id(userId).username("Test user").email("Test email").build();
        when(projectService.getUser(userId)).thenReturn(ResponseEntity.ok(expectedUserDto));

        ResponseEntity<UserDto> result = projectController.getUser(userId);

        assertEquals(expectedUserDto, result.getBody());
    }

    @Test
    void getUser_WhenUserDoesNotExist_ThrowsException() {
        long user = 999L;
        String errorMessage = "User with ID " + user + " not found";
        when(projectService.getUser(user)).thenThrow(new RuntimeException(errorMessage));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> projectController.getUser(user)
        );
        assertEquals(errorMessage, exception.getMessage());
    }
}