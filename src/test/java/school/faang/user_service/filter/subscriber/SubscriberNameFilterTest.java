package school.faang.user_service.filter.subscriber;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.subscriber.SubscriberFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SubscriberNameFilterTest {
    private User user1, user2, user3;
    private SubscriberNameFilter filter;
    private SubscriberFilterDto filterDto;

    @BeforeEach
    void setUp() {
        filter = new SubscriberNameFilter();

        filterDto = new SubscriberFilterDto();
        filterDto.setNamePattern("John");

        user1 = new User();
        user2 = new User();
        user3 = new User();
    }

    @Test
    void testIsApplicableWhenNamePatternIsSet() {
        assertTrue(filter.isApplicable(filterDto));
    }

    @Test
    void testTheApplicableWhenNamePatternIsNull() {
        filterDto.setNamePattern(null);
        assertFalse(filter.isApplicable(filterDto));
    }

    @Test
    void testApplyFilterMatchesOneUser() {
        user1.setUsername("JohnDoe");
        user2.setUsername("JaneSmith");
        applyAndAssertCount(Stream.of(user1, user2), filterDto, 1);
    }

    @Test
    void testApplyFilterMatchesMultipleUsers() {
        user1.setUsername("JohnDoe");
        user2.setUsername("Johnny");
        user3.setUsername("Alice");

        applyAndAssertCount(Stream.of(user1, user2, user3), filterDto, 2);
    }

    @Test
    void testApplyNoMatchingUsers() {
        user1.setUsername("Alice");
        user2.setUsername("Bob");
        applyAndAssertCount(Stream.of(user1, user2), filterDto, 0);
    }

    @Test
    void testApplyUserWithNullUsername() {
        user1.setUsername(null);
        user2.setUsername("JohnDoe");
        applyAndAssertCount(Stream.of(user1, user2), filterDto, 1);
    }

    private void applyAndAssertCount(Stream<User> users, SubscriberFilterDto filterDto, long expectedCount) {
        long actualCount = filter.apply(users, filterDto).count();
        assertEquals(expectedCount, actualCount);
    }
}