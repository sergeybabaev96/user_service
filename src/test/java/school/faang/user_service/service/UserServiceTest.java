package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserFilter;
import school.faang.user_service.filter.user.UserFilterByNamePattern;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    private static final String PATTERN = "Ivan";
    private static final UserFilterDto PRESET = UserFilterDto.builder().usernamePattern(PATTERN).build();
    private static final String CONTENT1 = "Ivan Ivanov";
    private static final String CONTENT2 = "Fedor Ivanov";
    private static final String CONTENT3 = "Fedor Fedorov";
    private static final int AMOUNT_CONTENT_MATCHING_PRESET = 2;


    @Mock
    private UserRepository userRepository;
    private List<UserFilter> userFilters;
    private UserService userService;

    private List<User> sentUsers;

    @BeforeEach
    void setUp() {
        userFilters = List.of(new UserFilterByNamePattern());
        userService = new UserService(userRepository, userFilters);
        sentUsers = List.of(
                User.builder().username(CONTENT1).build(),
                User.builder().username(CONTENT2).build(),
                User.builder().username(CONTENT3).build()
        );
    }

    @Test
    void getPremiumUsersWithOutFilters() {
        doReturn(sentUsers.stream()).when(userRepository).findPremiumUsers();
        Stream<User> returnedUsers = userService.getPremiumUsers(null);

        assertEquals(sentUsers.size(), returnedUsers.count());
    }

    @Test
    void getPremiumUsersWithFilters() {
        doReturn(sentUsers.stream()).when(userRepository).findPremiumUsers();
        Stream<User> returnedUsers = userService.getPremiumUsers(PRESET);

        assertEquals(AMOUNT_CONTENT_MATCHING_PRESET, returnedUsers.count());
    }
}