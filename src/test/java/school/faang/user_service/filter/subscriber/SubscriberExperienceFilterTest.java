package school.faang.user_service.filter.subscriber;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.subscriber.SubscriberFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SubscriberExperienceFilterTest {
    private User user1, user2, user3;
    private SubscriberExperienceFilter filter;
    private SubscriberFilterDto filterDto;

    @BeforeEach
    void setUp() {
        filter = new SubscriberExperienceFilter();
        filterDto = new SubscriberFilterDto();

        user1 = new User();
        user2 = new User();
        user3 = new User();
    }

    @Test
    void testIsApplicableWhenMinExperienceSet() {
        filterDto.setExperienceMin(1);
        filterDto.setExperienceMax(0);
        assertTrue(filter.isApplicable(filterDto));
    }

    @Test
    void testIsApplicableWhenMaxExperienceSet() {
        filterDto.setExperienceMin(0);
        filterDto.setExperienceMax(5);
        assertTrue(filter.isApplicable(filterDto));
    }

    @Test
    void testIsApplicableWhenBothMinAndMaxAreZero() {
        filterDto.setExperienceMin(0);
        filterDto.setExperienceMax(0);
        assertFalse(filter.isApplicable(filterDto));
    }

    @Test
    void testApplyFilterMatchesUsersWithinRange() {
        user1.setExperience(3);
        user2.setExperience(5);
        user3.setExperience(8);
        filterDto.setExperienceMin(3);
        filterDto.setExperienceMax(7);

        applyAndAssertCount(Stream.of(user1, user2, user3), filterDto, 2);
    }

    @Test
    void testApplyFilterMatchesExactMinAndMax() {
        user1.setExperience(2);
        user2.setExperience(5);
        user3.setExperience(8);
        filterDto.setExperienceMin(2);
        filterDto.setExperienceMax(8);

        applyAndAssertCount(Stream.of(user1, user2, user3), filterDto, 3);
    }

    @Test
    void testApplyNoMatchingUsers() {
        user1.setExperience(1);
        user2.setExperience(2);
        user3.setExperience(3);
        filterDto.setExperienceMin(5);
        filterDto.setExperienceMax(10);

        applyAndAssertCount(Stream.of(user1, user2, user3), filterDto, 0);
    }

    @Test
    void testApplyUserWithNullExperience() {
        user1.setExperience(null);
        user2.setExperience(5);
        filterDto.setExperienceMin(3);
        filterDto.setExperienceMax(7);

        applyAndAssertCount(Stream.of(user1, user2), filterDto, 1);
    }

    @Test
    void testApplyFilterWithMinGreaterThanMax() {
        user1.setExperience(4);
        user2.setExperience(6);
        filterDto.setExperienceMin(7);
        filterDto.setExperienceMax(5);

        applyAndAssertCount(Stream.of(user1, user2), filterDto, 0);
    }

    private void applyAndAssertCount(Stream<User> users, SubscriberFilterDto filterDto, long expectedCount) {
        long actualCount = filter.apply(users, filterDto).count();
        assertEquals(expectedCount, actualCount);
    }
}