package school.faang.user_service.service.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.GoalDataException;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;

    @Test
    void findUserById_findsWithRightUserId() {
        User user = new User();
        user.setId(1L);
        Optional<User> userOptional = Optional.of(user);
        ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
        when(userRepository.findById(user.getId())).thenReturn(userOptional);

        Optional<User> actualUserOptional = userRepository.findById(user.getId());

        verify(userRepository, times(1)).findById(userIdCaptor.capture());
        Long userIdCaptorValue = userIdCaptor.getValue();
        assertEquals(user.getId(), userIdCaptorValue);
        assertTrue(actualUserOptional.isPresent());
        assertEquals(userOptional.get(), actualUserOptional.get());
    }

    @Test
    void findUserById_notFindsWithWrongUserId() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(GoalDataException.class, () -> userService.findUserById(user.getId()));
    }


    @Test
    void saveUser() {
        User user = new User();
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        userService.saveUser(user);

        verify(userRepository, times(1)).save(userCaptor.capture());
        User userValue = userCaptor.getValue();
        assertEquals(user, userValue);
    }
}