package school.faang.user_service.controller;

import org.hibernate.annotations.Source;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.PremiumUserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.PremiumUserMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.service.UserService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PremiumUserControllerTest {
    private static final String CONTENT1 = "Ivan Ivanov";
    private static final String CONTENT2 = "Fedor Ivanov";
    private static final String CONTENT3 = "Fedor Fedorov";

    private List<User> sentUsers;
    private UserFilterDto userFilterDto;

    @Mock
    private UserService userService;
    @Mock
    private PremiumUserMapper premiumUserMapper;

    @InjectMocks
    private PremiumUserController userController;

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
    @DisplayName("PremiumUserController.  getPremiumUsers() WITH UserFilterDto")
    void getPremiumUsersWithFilterDto() {
        when(userService.getPremiumUsers(userFilterDto)).thenReturn(sentUsers);
        List<PremiumUserDto> returnedUsers = userController.getPremiumUsers(userFilterDto);

        assertEquals(sentUsers.size(), returnedUsers.size());
    }

    @Test
    @DisplayName("PremiumUserController.  getPremiumUsers() with OUT UserFilterDto")
    void getPremiumUsersWithOutFilterDto() {
        when(userService.getPremiumUsers(null)).thenReturn(sentUsers);
        List<PremiumUserDto> returnedUsers = userController.getPremiumUsers(null);

        assertEquals(sentUsers.size(), returnedUsers.size());
    }

}