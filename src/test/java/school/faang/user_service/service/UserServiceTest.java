package school.faang.user_service.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserViewDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.model.PreferredContact;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.service.user.UserService;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты сервиса пользователей")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private final long validUserId = 1L;
    private final long invalidUserId = 999L;
    private final User testUser = User.builder()
            .id(validUserId)
            .username("testUser")
            .email("test@example.com")
            .phone("1234567890")
            .preference(PreferredContact.EMAIL)
            .locale(Locale.ENGLISH)
            .build();
    private final UserViewDto userViewDto = new UserViewDto();
    private final UserDto userDto = UserDto.builder()
            .id(validUserId)
            .username("testUser")
            .email("test@example.com")
            .phone("1234567890")
            .preference(PreferredContact.EMAIL)
            .locale(Locale.ENGLISH)
            .build();

    @Nested
    @DisplayName("Тесты получения пользователя")
    class GetUserTests {
        @Test
        @DisplayName("Успешное получение пользователя по ID")
        void givenValidUserId_whenGetUser_thenReturnUserDto() {
            when(userRepository.findById(validUserId)).thenReturn(Optional.of(testUser));
            when(userMapper.toViewDto(testUser)).thenReturn(userViewDto);

            UserViewDto result = userService.getUser(validUserId);

            assertEquals(userViewDto, result);
            verify(userRepository).findById(validUserId);
            verify(userMapper).toViewDto(testUser);
        }

        @Test
        @DisplayName("Получение несуществующего пользователя вызывает исключение")
        void givenInvalidUserId_whenGetUser_thenThrowException() {
            when(userRepository.findById(invalidUserId)).thenReturn(Optional.empty());

            Exception exception = assertThrows(DataValidationException.class,
                    () -> userService.getUser(invalidUserId));

            assertEquals("Пользователь не найден", exception.getMessage());
            verify(userRepository).findById(invalidUserId);
        }
    }

    @Nested
    @DisplayName("Тесты получения сущности пользователя")
    class GetUserEntityTests {
        @Test
        @DisplayName("Успешное получение сущности пользователя")
        void givenValidUserId_whenGetUserEntity_thenReturnUser() {
            when(userRepository.findById(validUserId)).thenReturn(Optional.of(testUser));

            User result = userService.getUserEntity(validUserId);

            assertEquals(testUser, result);
            verify(userRepository).findById(validUserId);
        }

        @Test
        @DisplayName("Получение несуществующей сущности пользователя вызывает исключение")
        void givenInvalidUserId_whenGetUserEntity_thenThrowException() {
            when(userRepository.findById(invalidUserId)).thenReturn(Optional.empty());

            Exception exception = assertThrows(DataValidationException.class,
                    () -> userService.getUserEntity(invalidUserId));

            assertEquals("Пользователь не найден", exception.getMessage());
            verify(userRepository).findById(invalidUserId);
        }
    }

    @Nested
    @DisplayName("Тесты получения списка пользователей")
    class GetUsersByIdsTests {
        @Test
        @DisplayName("Успешное получение списка пользователей по IDs")
        void givenValidUserIds_whenGetUsersByIds_thenReturnUserList() {
            List<Long> ids = List.of(validUserId);
            when(userRepository.findAllById(ids)).thenReturn(List.of(testUser));
            when(userMapper.toViewDto(testUser)).thenReturn(userViewDto);

            List<UserViewDto> result = userService.getUsersByIds(ids);

            assertEquals(1, result.size());
            assertEquals(userViewDto, result.get(0));
            verify(userRepository).findAllById(ids);
            verify(userMapper).toViewDto(testUser);
        }

        @Test
        @DisplayName("Получение пустого списка при отсутствии пользователей")
        void givenNonExistingUserIds_whenGetUsersByIds_thenReturnEmptyList() {
            List<Long> ids = List.of(invalidUserId);
            when(userRepository.findAllById(ids)).thenReturn(List.of());

            List<UserViewDto> result = userService.getUsersByIds(ids);

            assertTrue(result.isEmpty());
            verify(userRepository).findAllById(ids);
        }
    }

    @Nested
    @DisplayName("Тесты получения базовой информации")
    class GetBasicUserInfoTests {
        @Test
        @DisplayName("Успешное получение базовой информации о пользователе")
        void givenValidUserId_whenGetUserForService_thenReturnBasicUserDto() {
            when(userRepository.findById(validUserId)).thenReturn(Optional.of(testUser));

            UserDto result = userService.getUserForService(validUserId);

            assertEquals(validUserId, result.getId());
            verify(userRepository).findById(validUserId);
        }

        @Test
        @DisplayName("Получение базовой информации о несуществующем пользователе вызывает исключение")
        void givenInvalidUserId_whenGetUserForService_thenThrowException() {
            when(userRepository.findById(invalidUserId)).thenReturn(Optional.empty());

            Exception exception = assertThrows(DataValidationException.class,
                    () -> userService.getUserForService(invalidUserId));

            assertEquals("Пользователь не найден", exception.getMessage());
            verify(userRepository).findById(invalidUserId);
        }
    }
}