package school.faang.user_service.service.subscription.filter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.subscription.SubscriptionUserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.TestData;

import java.util.List;
import java.util.stream.Stream;

class SubscriptionUserCityFilterTest {
    private SubscriptionUserFilterDto subscriptionUserFilterDto;
    private final SubscriptionUserCityFilter filter = new SubscriptionUserCityFilter();
    private boolean isApplicableActual;
    private boolean isApplicableExpected;
    private List<User> allUsers;

    @BeforeEach
    void setUp() {
        allUsers = TestData.getUsers();
    }

    @Test
    @DisplayName("Test positive applicability user filter by City")
    void isApplicableTest() {
        isApplicableExpected = true;
        subscriptionUserFilterDto = SubscriptionUserFilterDto.builder()
                .cityPattern("Moscow")
                .build();
        isApplicableActual = filter.isApplicable(subscriptionUserFilterDto);
        Assertions.assertEquals(isApplicableExpected, isApplicableActual);
    }

    @Test
    @DisplayName("Test negative applicability user filter by City")
    void isNotApplicableTest() {
        isApplicableExpected = false;
        subscriptionUserFilterDto = SubscriptionUserFilterDto.builder()
                .cityPattern("")
                .build();
        isApplicableActual = filter.isApplicable(subscriptionUserFilterDto);
        Assertions.assertEquals(isApplicableExpected, isApplicableActual);

        subscriptionUserFilterDto = SubscriptionUserFilterDto.builder()
                .build();
        isApplicableActual = filter.isApplicable(subscriptionUserFilterDto);
        Assertions.assertEquals(isApplicableExpected, isApplicableActual);
    }

    @Test
    @DisplayName("Test result of user filter by City")
    void applyTrueSearch() {
        subscriptionUserFilterDto = SubscriptionUserFilterDto.builder()
                .cityPattern("Moscow")
                .build();
        Stream<User> userStream = filter.apply(allUsers.stream(), subscriptionUserFilterDto);
        List<User> actualUsers = userStream.toList();

        List<User> expectedUsers = allUsers.stream()
                .filter(u -> u.getId() == 1L)
                .toList();

        Assertions.assertEquals(actualUsers, expectedUsers);
    }

    @Test
    @DisplayName("Test empty result of user filter by City")
    void applyFalseSearch() {
        subscriptionUserFilterDto = SubscriptionUserFilterDto.builder()
                .cityPattern("sdfsdfsdfsdf")
                .build();
        Stream<User> userStream = filter.apply(allUsers.stream(), subscriptionUserFilterDto);
        List<User> actualUsers = userStream.toList();
        Assertions.assertEquals(actualUsers.size(), 0);
    }
}