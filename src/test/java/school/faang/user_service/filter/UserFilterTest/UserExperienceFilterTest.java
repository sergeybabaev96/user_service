package school.faang.user_service.filter.UserFilterTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.user.UserExperienceFilter;
import school.faang.user_service.filter.user.UserFilter;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserExperienceFilterTest {
    private UserFilter filter;
    private UserFilterDto filterDto;

    private UserFilterDto createUserFilterDto(int minExperience, int maxExperience) {
        return new UserFilterDto(
                "",
                null,
                minExperience,
                maxExperience
        );
    }

    private User createUser(int experience) {
        User user = new User();
        user.setExperience(experience);
        return user;
    }

    @BeforeEach
    public void setUp() {
        filter = new UserExperienceFilter();
    }

    @Test
    void testIsApplicableWhenMinExpSet() {
        filterDto = createUserFilterDto(1, 0);
        assertTrue(filter.isApplicable(filterDto));
    }

    @Test
    void testIsApplicableWhenMaxExpSet() {
        filterDto = createUserFilterDto(0, 1);
        assertTrue(filter.isApplicable(filterDto));
    }

    @Test
    void testIsApplicableWhenExpNotSet() {
        filterDto = createUserFilterDto(0, 0);
        assertFalse(filter.isApplicable(filterDto));
    }

    @Test
    void testIsApplicableInRange() {
        User firstUser = createUser(39);
        User secondUser = createUser(77);
        filterDto = createUserFilterDto(10, 70);
        applyAndAssertCount(Stream.of(firstUser, secondUser), filterDto, 1);
    }

    @Test
    void testIsApplicableNotMatches() {
        User firstUser = createUser(39);
        User secondUser = createUser(77);
        filterDto = createUserFilterDto(85, 0);
        applyAndAssertCount(Stream.of(firstUser, secondUser), filterDto, 0);
    }

    private void applyAndAssertCount(Stream<User> users, UserFilterDto filterDto, long expectedCount) {
        long actualCount = filter.apply(users, filterDto).count();
        assertEquals(expectedCount, actualCount);
    }

}
