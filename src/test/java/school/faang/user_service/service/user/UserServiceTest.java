package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private final long userId = 1L;
    private final User user = User.builder().id(userId).build();

    @Test
    public void testIsWithinGoalLimit() {
        Optional<User> foundUser = Optional.of(User.builder()
                .goals(List.of(
                        Goal.builder().build(),
                        Goal.builder().build()))
                .build());

        boolean answer = getResult(foundUser);

        verify(userRepository, times(1)).findById(userId);
        assertTrue(answer);
    }

    @Test
    public void testIsWithinOverGoalLimit() {
        Optional<User> foundUser = Optional.of(User.builder()
                .goals(List.of(
                        Goal.builder().build(),
                        Goal.builder().build(),
                        Goal.builder().build(),
                        Goal.builder().build()))
                .build());

        boolean result = getResult(foundUser);

        verify(userRepository, times(1)).findById(userId);
        assertFalse(result);
    }

    @Test
    public void testIsWithinGoalLimitWithNotFoundUser() {
        assertThrows(EntityNotFoundException.class, () -> getResult(Optional.empty()));
    }


    private boolean getResult(Optional<User> foundUser) {
        when(userRepository.findById(userId)).thenReturn(foundUser);
        return userService.isWithinGoalLimit(userId);
    }

@Test
public void save_shouldSave() {
    userService.save(user);

    Mockito.verify(userRepository, Mockito.times(1)).save(user);
}

@Test
public void findById_shouldFind() {
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    Optional<User> result = userService.findById(userId);

    Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
    assertTrue(result.isPresent());
    assertEquals(user, result.get());
}

}