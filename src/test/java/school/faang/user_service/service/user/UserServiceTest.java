package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private final long id = 1;

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    public void testIsWithinGoalLimit() {
        Optional<User> foundUser = Optional.of(User.builder()
                .goals(List.of(
                        Goal.builder().build(),
                        Goal.builder().build()))
                .build());

        boolean answer = getResult(foundUser);

        verify(userRepository, times(1)).findById(id);
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

        verify(userRepository, times(1)).findById(id);
        assertFalse(result);
    }

    @Test
    public void testIsWithinGoalLimitWithNotFoundUser() {
        assertThrows(EntityNotFoundException.class, () -> getResult(Optional.empty()));
    }


    private boolean getResult(Optional<User> foundUser) {
        when(userRepository.findById(id)).thenReturn(foundUser);
        return userService.isWithinGoalLimit(id);
    }
}