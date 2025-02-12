package school.faang.user_service.filter.subscriber;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.subscriber.SubscriberFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class SubscriberEmailFilterTest {
    private User user1, user2, user3;
    private SubscriberEmailFilter filter;
    private SubscriberFilterDto filterDto;

    @BeforeEach
    void setUp() {
        filter = new SubscriberEmailFilter();

        filterDto = new SubscriberFilterDto();
        filterDto.setEmailPattern("vik");

        user1 = new User();
        user2 = new User();
        user3 = new User();
    }

    @Test
    void testIsApplicableWhenEmailPatternIsSet() {
        assertTrue(filter.isApplicable(filterDto));
    }

    @Test
    void testIsApplicableWhenEmailPatternIsNull() {
        filterDto.setEmailPattern(null);
        assertFalse(filter.isApplicable(filterDto));
    }

    @Test
    void testApplyFilterMatchesOneUser() {
        user1.setEmail("viktor@outlook.com");
        user2.setEmail("bob@outlook.com");

        applyAndAssertCount(Stream.of(user1, user2), filterDto, 1);
    }

    @Test
    void testApplyFilterMatchesMultipleUsers() {
        user1.setEmail("viktor@outlook.com");
        user2.setEmail("vikki@outlook.com");
        user3.setEmail("bob@outlook.com");

        applyAndAssertCount(Stream.of(user1, user2, user3), filterDto, 2);
    }

    @Test
    void testApplyNoMatchingUsers() {
        user1.setEmail("bob@outlook.com");
        user1.setEmail("doe@outlook.com");

        applyAndAssertCount(Stream.of(user1, user2), filterDto, 0);
    }

    @Test
    void testApplyUserWithNullEmail() {
        user1.setEmail(null);
        user2.setEmail("viktor@outlook.com");

        applyAndAssertCount(Stream.of(user1, user2), filterDto, 1);
    }

    private void applyAndAssertCount(Stream<User> users, SubscriberFilterDto filterDto, long expectedCount) {
        long actualCount = filter.apply(users, filterDto).count();
        assertEquals(expectedCount, actualCount);
    }
}