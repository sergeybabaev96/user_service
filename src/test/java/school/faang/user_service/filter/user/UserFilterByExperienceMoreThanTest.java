package school.faang.user_service.filter.user;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UserFilterByExperienceMoreThanTest {
    private static final Integer PATTERN1 = 10;
    private static final Integer PATTERN2 = 100;
    private static final Integer CONTENT1 = 9;
    private static final Integer CONTENT2 = 10;
    private static final Integer CONTENT3 = 11;

    private static final UserFilterDto PRESET0 = new UserFilterDto();
    private static final UserFilterDto PRESET1 = UserFilterDto.builder().experienceMoreThan(PATTERN1).build();
    private static final UserFilterDto PRESET2 = UserFilterDto.builder().experienceMoreThan(PATTERN2).build();

    private static UserFilter filter;

    private Stream<User> users;

    @BeforeAll
    static void init() {
        filter = new UserFilterByExperienceMoreThan();
    }

    @BeforeEach
    void setUp() {
        users = Stream.of(
                User.builder().experience(CONTENT1).build(),
                User.builder().experience(CONTENT2).build(),
                User.builder().experience(CONTENT3).build()
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