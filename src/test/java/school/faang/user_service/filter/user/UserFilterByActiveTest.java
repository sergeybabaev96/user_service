package school.faang.user_service.filter.user;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UserFilterByActiveTest {
    private static final boolean PATTERN1 = true;
    private static final boolean PATTERN2 = false;
    private static final boolean CONTENT1 = true;
    private static final boolean CONTENT2 = false;
    private static final boolean CONTENT3 = true;

    private static final UserFilterDto PRESET0 = new UserFilterDto();
    private static final UserFilterDto PRESET1 = UserFilterDto.builder().active(PATTERN1).build();
    private static final UserFilterDto PRESET2 = UserFilterDto.builder().active(PATTERN2).build();

    private static UserFilter filter;

    private Stream<User> users;

    @BeforeAll
    static void init() {
        filter = new UserFilterByActive();
    }

    @BeforeEach
    void setUp() {
        users = Stream.of(
                User.builder().active(CONTENT1).build(),
                User.builder().active(CONTENT2).build(),
                User.builder().active(CONTENT3).build()
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
    void applyWillReturnOneElements() {
        List<User> filteredUsers = filter.apply(users, PRESET2).toList();

        assertEquals(1, filteredUsers.size());
    }
}