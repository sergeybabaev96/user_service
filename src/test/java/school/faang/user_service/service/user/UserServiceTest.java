package school.faang.user_service.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.CountryRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private final UserRepository userRepositoryMock = Mockito.mock(UserRepository.class);
    private final CountryRepository countryRepositoryMock = Mockito.mock(CountryRepository.class);
    private final UserAvatarService userAvatarServiceMock = Mockito.mock(UserAvatarService.class);
    private final UserService userService = new UserService(
            null, userRepositoryMock, countryRepositoryMock, null, null, userAvatarServiceMock, null
    );

    @Test
    void testRegisterUser() {
        Country country = new Country();
        country.setId(1L);

        Mockito.when(countryRepositoryMock.findById(1L))
                .thenReturn(Optional.of(country));
        Mockito.when(userRepositoryMock.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User user = userService.registerUser("username", "email@example.com", "password", 1L);
        assertNotNull(user);
        assertEquals("username", user.getUsername());
    }
}
