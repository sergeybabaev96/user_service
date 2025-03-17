package school.faang.user_service.filter.user;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UserFilterByCreatedBeforeTest {
    private static final LocalDateTime PATTERN1 = LocalDateTime.now();
    private static final LocalDateTime PATTERN2 = LocalDateTime.now().minusMonths(2);
    private static final LocalDateTime CONTENT1 = LocalDateTime.now().minusMonths(1);
    private static final LocalDateTime CONTENT2 = LocalDateTime.now().minusDays(1);
    private static final LocalDateTime CONTENT3 = LocalDateTime.now();

    private static final UserFilterDto PRESET0 = new UserFilterDto();
    private static final UserFilterDto PRESET1 = UserFilterDto.builder().createdBefore(PATTERN1).build();
    private static final UserFilterDto PRESET2 = UserFilterDto.builder().createdBefore(PATTERN2).build();

    private static UserFilter filter;

    private Stream<User> users;

    @BeforeAll
    static void init() {
        filter = new UserFilterByCreatedBefore();
    }

    @BeforeEach
    void setUp() {
        users = Stream.of(
                User.builder().createdAt(CONTENT1).build(),
                User.builder().createdAt(CONTENT2).build(),
                User.builder().createdAt(CONTENT3).build()
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