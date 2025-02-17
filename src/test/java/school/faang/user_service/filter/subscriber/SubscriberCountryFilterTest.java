package school.faang.user_service.filter.subscriber;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.subscriber.SubscriberFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.Country;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SubscriberCountryFilterTest {
    private Country country1, country2, country3;
    private User user1, user2, user3;
    private SubscriberCountryFilter filter;
    private SubscriberFilterDto filterDto;

    @BeforeEach
    void setUp() {
        filter = new SubscriberCountryFilter();

        filterDto = new SubscriberFilterDto();
        filterDto.setCountryPattern("United");

        country1 = new Country();
        country2 = new Country();
        country3 = new Country();

        user1 = new User();
        user2 = new User();
        user3 = new User();
    }

    @Test
    void testIsApplicableWhenCountryPatternIsSet() {
        assertTrue(filter.isApplicable(filterDto));
    }

    @Test
    void testIsApplicableWhenCountryPatternIsNull() {
        filterDto.setCountryPattern(null);
        assertFalse(filter.isApplicable(filterDto));
    }

    @Test
    void testApplyFilterMatchesOneUser() {
        country1.setTitle("United States");
        country2.setTitle("Canada");
        user1.setCountry(country1);
        user2.setCountry(country2);

        applyAndAssertCount(Stream.of(user1, user2), filterDto, 1);
    }

    @Test
    void testApplyFilterMatchesMultipleUsers() {
        country1.setTitle("United States");
        country2.setTitle("United Kingdom");
        country3.setTitle("Canada");
        user1.setCountry(country1);
        user2.setCountry(country2);
        user3.setCountry(country3);

        applyAndAssertCount(Stream.of(user1, user2, user3), filterDto, 2);
    }

    @Test
    void testApplyNoMatchingUsers() {
        country1.setTitle("Canada");
        country2.setTitle("Mexico");
        user1.setCountry(country1);
        user2.setCountry(country2);

        applyAndAssertCount(Stream.of(user1, user2), filterDto, 0);
    }

    @Test
    void testApplyUserWithNullCountry() {
        country2.setTitle("United States");
        user1.setCountry(null);
        user2.setCountry(country2);

        applyAndAssertCount(Stream.of(user1, user2), filterDto, 1);
    }

    @Test
    void testApplyUserWithNullCountryTitle() {
        country1.setTitle(null);
        country2.setTitle("United States");
        user1.setCountry(country1);
        user2.setCountry(country2);

        applyAndAssertCount(Stream.of(user1, user2), filterDto, 1);
    }

    private void applyAndAssertCount(Stream<User> users, SubscriberFilterDto filterDto, long expectedCount) {
        long actualCount = filter.apply(users, filterDto).count();
        assertEquals(expectedCount, actualCount);
    }
}