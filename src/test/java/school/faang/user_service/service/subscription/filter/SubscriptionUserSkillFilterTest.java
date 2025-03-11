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

class SubscriptionUserSkillFilterTest {

    private SubscriptionUserFilterDto subscriptionUserFilterDto;
    private final SubscriptionUserSkillFilter filter = new SubscriptionUserSkillFilter();
    private boolean isApplicableActual;
    private boolean isApplicableExpected;
    private List<User> users;

    @BeforeEach
    void setUp() {
        users = TestData.getUsers();
    }

    @Test
    @DisplayName("Test true applicability user filter by Skill")
    void isApplicableTest() {
        isApplicableExpected = true;
        subscriptionUserFilterDto = SubscriptionUserFilterDto.builder()
                .skillPattern("Skill 1")
                .build();
        isApplicableActual = filter.isApplicable(subscriptionUserFilterDto);
        Assertions.assertEquals(isApplicableExpected, isApplicableActual);
    }

    @Test
    @DisplayName("Test false applicability user filter by Skill")
    void isNotApplicableTest() {
        isApplicableExpected = false;
        subscriptionUserFilterDto = SubscriptionUserFilterDto.builder()
                .skillPattern("")
                .build();
        isApplicableActual = filter.isApplicable(subscriptionUserFilterDto);
        Assertions.assertEquals(isApplicableExpected, isApplicableActual);

        subscriptionUserFilterDto = SubscriptionUserFilterDto.builder()
                .build();
        isApplicableActual = filter.isApplicable(subscriptionUserFilterDto);
        Assertions.assertEquals(isApplicableExpected, isApplicableActual);
    }

    @Test
    @DisplayName("Test positive result of user filter by Skill")
    void applyTrueSearch() {
        subscriptionUserFilterDto = SubscriptionUserFilterDto.builder()
                .skillPattern("Skill 3")
                .build();
        Stream<User> userStream = filter.apply(users.stream(), subscriptionUserFilterDto);
        List<User> actualUsers = userStream.toList();

        List<User> expectedUsers = users.stream()
                .filter(u -> (u.getId() == 2L || u.getId() == 3L))
                .toList();

        Assertions.assertEquals(expectedUsers, actualUsers);

    }

    @Test
    @DisplayName("Test empty result of user filter by Skill")
    void applyFalseSearch() {
        subscriptionUserFilterDto = SubscriptionUserFilterDto.builder()
                .skillPattern("sdfsdfsdfsdf")
                .build();
        Stream<User> userStream = filter.apply(users.stream(), subscriptionUserFilterDto);
        List<User> actualUsers = userStream.toList();
        Assertions.assertEquals(actualUsers.size(), 0);
    }
}