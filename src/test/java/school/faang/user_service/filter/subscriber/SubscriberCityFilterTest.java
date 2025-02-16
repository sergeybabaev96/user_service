package school.faang.user_service.filter.subscriber;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.subscriber.SubscriberFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SubscriberCityFilterTest {
    private User user1, user2, user3;
    private SubscriberCityFilter filter;
    private SubscriberFilterDto filterDto;

    @BeforeEach
    void setUp() {
        filter = new SubscriberCityFilter();
        filterDto = new SubscriberFilterDto();

        user1 = new User();
        user2 = new User();
        user3 = new User();
    }

    @Test
    void testIsApplicableWhenCityPatternIsSet() {
        filterDto.setCityPattern("New York");
        assertTrue(filter.isApplicable(filterDto), "Фильтр должен быть применим при наличии cityPattern.");
    }

    @Test
    void testIsApplicableWhenCityPatternIsNull() {
        filterDto.setCityPattern(null);
        assertFalse(filter.isApplicable(filterDto), "Фильтр не должен быть применим без cityPattern.");
    }

    @Test
    void testApplyFilterMatchesOneUser() {
        user1.setCity("New York");
        user2.setCity("Los Angeles");
        filterDto.setCityPattern("New York");

        applyAndAssertCount(Stream.of(user1, user2), filterDto, 1);
    }

    @Test
    void testApplyFilterMatchesMultipleUsers() {
        user1.setCity("New York");
        user2.setCity("Newark");
        user3.setCity("Los Angeles");
        filterDto.setCityPattern("New");

        applyAndAssertCount(Stream.of(user1, user2, user3), filterDto, 2);
    }

    @Test
    void testApplyNoMatchingUsers() {
        user1.setCity("Los Angeles");
        user2.setCity("Chicago");
        filterDto.setCityPattern("New York");

        applyAndAssertCount(Stream.of(user1, user2), filterDto, 0);
    }

    @Test
    void testApply_UserWithNullCity() {
        user1.setCity(null);
        user2.setCity("New York");
        filterDto.setCityPattern("New York");

        applyAndAssertCount(Stream.of(user1, user2), filterDto, 1);
    }

    private void applyAndAssertCount(Stream<User> users, SubscriberFilterDto filterDto, long expectedCount) {
        long actualCount = filter.apply(users, filterDto).count();
        assertEquals(expectedCount, actualCount);
    }
}