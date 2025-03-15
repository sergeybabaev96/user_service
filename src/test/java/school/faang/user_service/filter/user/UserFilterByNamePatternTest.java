package school.faang.user_service.filter.user;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UserFilterByNamePatternTest {
    private static final String PATTERN1 = "Ivan";
    private static final String PATTERN2 = "Petr";
    private static final String CONTENT1 = "Ivan Ivanov";
    private static final String CONTENT2 = "Fedor Ivanov";
    private static final String CONTENT3 = "Fedor Fedorov";

    private static final UserFilterDto PRESET0 = new UserFilterDto();
    private static final UserFilterDto PRESET1 = UserFilterDto.builder().usernamePattern(PATTERN1).build();
    private static final UserFilterDto PRESET2 = UserFilterDto.builder().usernamePattern(PATTERN2).build();

    private static UserFilter filter;

    private Stream<User> users;

    @BeforeAll
    static void init() {
        filter = new UserFilterByNamePattern();
    }

    @BeforeEach
    void setUp() {
        users = Stream.of(
                User.builder().username(CONTENT1).build(),
                User.builder().username(CONTENT2).build(),
                User.builder().username(CONTENT3).build()
        );
    }

    @Test
    void isApplicableFalse() {
        assertFalse(filter.isApplicable(PRESET0));
    }

    @Test
    void isApplicableTrue() {
        assertTrue(filter.isApplicable(PRESET1));
        assertTrue(filter.isApplicable(PRESET2));
    }

    @Test
    void applyWillReturnTwoElements() {
        List<User> filteredUsers = filter.apply(users, PRESET1).toList();

        assertEquals(2, filteredUsers.size());
    }

    @Test
    void applyWillReturnZeroElements() {
        List<User> filteredUsers = filter.apply(users, PRESET2).toList();

        assertEquals(0, filteredUsers.size());
    }
}