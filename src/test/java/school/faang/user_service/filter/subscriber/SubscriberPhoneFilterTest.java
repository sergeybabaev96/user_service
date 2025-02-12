package school.faang.user_service.filter.subscriber;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.subscriber.SubscriberFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class SubscriberPhoneFilterTest {
    private User user1, user2, user3;
    private SubscriberPhoneFilter filter;
    private SubscriberFilterDto filterDto;

    @BeforeEach
    void setUp() {
        filter = new SubscriberPhoneFilter();
        filterDto = new SubscriberFilterDto();

        user1 = new User();
        user2 = new User();
        user3 = new User();
        user1.setPhone("+79990001111");
        user2.setPhone("+79990001122");
        user3.setPhone("+79990002222");
    }

    @Test
    void testIsApplicableWhenPhonePatternIsSet() {
        filterDto.setPhonePattern("123");
        assertTrue(filter.isApplicable(filterDto));
    }

    @Test
    void testIsApplicableWhenPhonePatternIsNull() {
        filterDto.setPhonePattern(null);
        assertFalse(filter.isApplicable(filterDto));
    }

    @Test
    void testApplyFilterMatchesOneUser() {
        filterDto.setPhonePattern("111");
        applyAndAssertCount(Stream.of(user1, user2), filterDto, 1);
    }

    @Test
    void testApplyFilterMatchesMultipleUsers() {
        filterDto.setPhonePattern("011");
        applyAndAssertCount(Stream.of(user1, user2, user3), filterDto, 2);
    }

    @Test
    void testApplyNoMatchingUsers() {
        filterDto.setPhonePattern("555");
        applyAndAssertCount(Stream.of(user1, user2), filterDto, 0);
    }

    @Test
    void testApplyUserWithNullPhone() {
        user1.setPhone(null);
        filterDto.setPhonePattern("122");
        applyAndAssertCount(Stream.of(user1, user2), filterDto, 1);
    }

    private void applyAndAssertCount(Stream<User> users, SubscriberFilterDto filterDto, long expectedCount) {
        long actualCount = filter.apply(users, filterDto).count();
        assertEquals(expectedCount, actualCount);
    }
}