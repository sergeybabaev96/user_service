package school.faang.user_service.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.UserService;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    private static final String CONTENT1 = "Ivan Ivanov";
    private static final String CONTENT2 = "Fedor Ivanov";
    private static final String CONTENT3 = "Fedor Fedorov";

    private List<User> sentUsers;
    private UserFilterDto userFilterDto;

    @Mock
    private UserService userService;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        userFilterDto = new UserFilterDto();
        sentUsers = List.of(
                User.builder().username(CONTENT1).build(),
                User.builder().username(CONTENT2).build(),
                User.builder().username(CONTENT3).build()
        );
    }

    @Test
    void getPremiumUsers() {
        when(userService.getPremiumUsers(userFilterDto)).thenReturn(sentUsers.stream());
        List<UserDto> returnedUsers = userController.getPremiumUsers(userFilterDto);

        assertEquals(sentUsers.size(), returnedUsers.size());
    }
}