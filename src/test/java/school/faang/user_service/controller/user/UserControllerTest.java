package school.faang.user_service.controller.user;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.user.UserService;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private UserMapper userMapper;
    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;

    private static Long validUserId = 1L;
    private static Long invalidUserId = -2L;

    @Test
    public void testDeactivateUserWithValidUserId() {
        userController.deactivateUser(validUserId);

        Mockito.verify(userService, Mockito.times(1)).deactivateUser(validUserId);
    }

    @Test
    public void testDeactivateUserWithInvalidUserId() {
        Assert.assertThrows(DataValidationException.class,
                () -> userController.deactivateUser(invalidUserId));
    }
}
