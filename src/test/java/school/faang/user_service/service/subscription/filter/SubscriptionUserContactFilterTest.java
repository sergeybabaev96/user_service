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

class SubscriptionUserContactFilterTest {
    private SubscriptionUserFilterDto subscriptionUserFilterDto;
    private final SubscriptionUserContactFilter filter = new SubscriptionUserContactFilter();
    private boolean isApplicableActual;
    private boolean isApplicableExpected;
    private List<User> users;

    @BeforeEach
    void setUp() {
        users = TestData.getUsers();
    }

    @Test
    @DisplayName("Test true applicability user filter by Contact")
    void isApplicableTest() {
        isApplicableExpected = true;
        subscriptionUserFilterDto = SubscriptionUserFilterDto.builder()
                .contactPattern("ContactName")
                .build();
        isApplicableActual = filter.isApplicable(subscriptionUserFilterDto);
        Assertions.assertEquals(isApplicableExpected, isApplicableActual);
    }

    @Test
    @DisplayName("Test false applicability user filter by Contact")
    void isNotApplicableTest() {
        isApplicableExpected = false;
        subscriptionUserFilterDto = SubscriptionUserFilterDto.builder()
                .contactPattern("")
                .build();
        isApplicableActual = filter.isApplicable(subscriptionUserFilterDto);
        Assertions.assertEquals(isApplicableExpected, isApplicableActual);

        subscriptionUserFilterDto = SubscriptionUserFilterDto.builder()
                .build();
        isApplicableActual = filter.isApplicable(subscriptionUserFilterDto);
        Assertions.assertEquals(isApplicableExpected, isApplicableActual);
    }

    @Test
    @DisplayName("Test positive result of user filter by Contact")
    void applyTrueSearch() {
        subscriptionUserFilterDto = SubscriptionUserFilterDto.builder()
                .contactPattern("Contact 1")
                .build();
        Stream<User> userStream = filter.apply(users.stream(), subscriptionUserFilterDto);
        List<User> actualUsers = userStream.toList();

        List<User> expectedUsers = users.stream()
                .filter(u -> u.getId() == 1L)
                .toList();

        Assertions.assertEquals(expectedUsers, actualUsers);

    }

    @Test
    @DisplayName("Test empty result of user filter by Contact")
    void applyFalseSearch() {
        subscriptionUserFilterDto = SubscriptionUserFilterDto.builder()
                .contactPattern("sdfsdfsdfsdf")
                .build();
        Stream<User> userStream = filter.apply(users.stream(), subscriptionUserFilterDto);
        List<User> actualUsers = userStream.toList();
        Assertions.assertEquals(actualUsers.size(), 0);
    }
}