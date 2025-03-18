package school.faang.user_service.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GetUserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    @Test
    @DisplayName("getUser(id). Positive")
    void getUser() {
        Long id = 1L;
        Optional<User> result = Optional.of(new User());

        Mockito.when(userRepository.findById(id)).thenReturn(result);

        assertEquals(result, userService.getUser(id));
        Mockito.verify(userRepository, Mockito.times(1)).findById(id);
    }

    @Test
    @DisplayName("getUsers(ids). Positive")
    void getUsers() {
        List<Long> ids = List.of(1L, 2L, 3L);
        List<User> result = List.of(new User(), new User(), new User());

        Mockito.when(userRepository.findAllById(ids)).thenReturn(result);

        assertEquals(result, userService.getUsers(ids));
        Mockito.verify(userRepository, Mockito.times(1)).findAllById(ids);
    }
}