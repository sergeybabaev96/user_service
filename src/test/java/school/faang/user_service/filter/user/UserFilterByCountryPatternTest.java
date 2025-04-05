package school.faang.user_service.filter.user;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UserFilterByCountryPatternTest {
    private static final String PATTERN1 = "Pattern1";
    private static final String PATTERN2 = "Pattern2";
    private static Country CONTENT1;
    private static Country CONTENT2;
    private static Country CONTENT3;

    private static final UserFilterDto PRESET0 = new UserFilterDto();
    private static final UserFilterDto PRESET1 = UserFilterDto.builder().countryPattern(PATTERN1).build();
    private static final UserFilterDto PRESET2 = UserFilterDto.builder().countryPattern(PATTERN2).build();

    private static UserFilter filter;

    private Stream<User> users;

    @BeforeAll
    static void init() {
        filter = new UserFilterByCountryPattern();
        CONTENT1 = new Country();
        CONTENT1.setTitle("Pattern1 Pattern1ov");
        CONTENT2 = new Country();
        CONTENT2.setTitle("Other Text Pattern1");
        CONTENT3 = new Country();
        CONTENT3.setTitle("Other Text Pattern3");
    }

    @BeforeEach
    void setUp() {
        users = Stream.of(
                User.builder().country(CONTENT1).build(),
                User.builder().country(CONTENT2).build(),
                User.builder().country(CONTENT3).build()
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