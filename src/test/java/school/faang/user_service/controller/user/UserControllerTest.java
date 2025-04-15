package school.faang.user_service.controller.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserViewDto;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.user.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты контроллера пользователей")
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Nested
    @DisplayName("Тесты получения пользователя по ID")
    class GetUserTests {
        @Test
        @DisplayName("Успешное получение пользователя - должен вернуть статус 200 OK")
        void givenValidUserId_whenGetUser_thenReturnUserAndOkStatus() {
            long userId = 1L;
            UserViewDto expectedUser = new UserViewDto();
            expectedUser.setId(userId);

            when(userService.getUser(userId)).thenReturn(expectedUser);

            ResponseEntity<UserViewDto> response = userController.getUser(userId);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(expectedUser, response.getBody());
            verify(userService).getUser(userId);
        }

        @Test
        @DisplayName("Получение несуществующего пользователя - должен вернуть статус 404 Not Found")
        void givenInvalidUserId_whenGetUser_thenReturnNotFoundStatus() {
            long userId = 999L;

            when(userService.getUser(userId))
                    .thenThrow(new DataValidationException("User not found"));

            assertThrows(DataValidationException.class, () -> {
                userController.getUser(userId);
            });

            verify(userService).getUser(userId);
        }
    }

    @Nested
    @DisplayName("Тесты получения пользователей по IDs")
    class GetUsersByIdsTests {
        @Test
        @DisplayName("Успешное получение списка пользователей - должен вернуть статус 200 OK")
        void givenValidUserIds_whenGetUsersByIds_thenReturnUsersListAndOkStatus() {
            List<Long> userIds = List.of(1L, 2L);
            List<UserViewDto> expectedUsers = List.of(new UserViewDto(), new UserViewDto());

            when(userService.getUsersByIds(userIds)).thenReturn(expectedUsers);

            ResponseEntity<List<UserViewDto>> response = userController.getUsersByIds(userIds);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(expectedUsers, response.getBody());
            verify(userService).getUsersByIds(userIds);
        }

        @Test
        @DisplayName("Получение списка с пустым запросом - должен вернуть статус 200 OK")
        void givenEmptyList_whenGetUsersByIds_thenReturnEmptyListAndOkStatus() {
            List<Long> userIds = List.of();
            List<UserViewDto> expectedUsers = List.of();

            when(userService.getUsersByIds(userIds)).thenReturn(expectedUsers);

            ResponseEntity<List<UserViewDto>> response = userController.getUsersByIds(userIds);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(response.getBody().isEmpty());
            verify(userService).getUsersByIds(userIds);
        }
    }

    @Nested
    @DisplayName("Тесты получения базовой информации о пользователе")
    class GetBasicUserInfoTests {
        @Test
        @DisplayName("Успешное получение базовой информации - должен вернуть статус 200 OK")
        void givenValidUserId_whenGetBasicUserInfo_thenReturnUserDtoAndOkStatus() {
            long userId = 1L;
            UserDto expectedUser = UserDto.builder()
                    .id(userId)
                    .build();

            when(userService.getUserBasicInfo(userId)).thenReturn(expectedUser);

            ResponseEntity<UserDto> response = userController.getUserForService(userId);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(expectedUser, response.getBody());
            verify(userService).getUserBasicInfo(userId);
        }

        @Test
        @DisplayName("Получение несуществующего пользователя - должен вернуть статус 404 Not Found")
        void givenInvalidUserId_whenGetBasicUserInfo_thenReturnNotFoundStatus() {
            long userId = 999L;

            when(userService.getUserBasicInfo(userId))
                    .thenThrow(new DataValidationException("User not found"));

            assertThrows(DataValidationException.class, () -> {
                userController.getUserForService(userId);
            });

            verify(userService).getUserBasicInfo(userId);
        }
    }
}