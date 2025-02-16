package school.faang.user_service.filter.subscriber;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.subscriber.SubscriberFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SubscriberAboutFilterTest {
    private SubscriberAboutFilter filter;
    private SubscriberFilterDto filterDto;
    private User user1, user2, user3;

    @BeforeEach
    void setUp() {
        filter = new SubscriberAboutFilter();
        filterDto = new SubscriberFilterDto();

        user1 = new User();
        user2 = new User();
        user3 = new User();
        user1.setAboutMe("I am a software developer");
        user2.setAboutMe("I am a software engineer");
        user3.setAboutMe("I am studying now");
    }

    @Test
    void testIsApplicableWhenAboutPatternIsSet() {
        filterDto.setAboutPattern("I am");
        assertTrue(filter.isApplicable(filterDto));
    }

    @Test
    void testIsApplicableWhenAboutPatternIsNull() {
        filterDto.setAboutPattern(null);
        assertFalse(filter.isApplicable(filterDto));
    }

    @Test
    void testApplyFilterMatchesOneUser() {
        filterDto.setAboutPattern("developer");
        applyAndAssertCount(Stream.of(user1, user2), filterDto, 1);
    }

    @Test
    void testApplyFilterMatchesMultipleUsers() {
        filterDto.setAboutPattern("software");
        applyAndAssertCount(Stream.of(user1, user2, user3), filterDto, 2);
    }

    @Test
    void testApplyNoMatchingUsers() {
        filterDto.setAboutPattern("because");
        applyAndAssertCount(Stream.of(user1, user2, user3), filterDto, 0);
    }

    @Test
    void testApplyUserWithNullAbout() {
        user1.setAboutMe(null);
        filterDto.setAboutPattern("now");
        applyAndAssertCount(Stream.of(user1, user2, user3), filterDto, 1);
    }

    private void applyAndAssertCount(Stream<User> users, SubscriberFilterDto filterDto, long expectedCount) {
        long actualCount = filter.apply(users, filterDto).count();
        assertEquals(expectedCount, actualCount);
    }
}