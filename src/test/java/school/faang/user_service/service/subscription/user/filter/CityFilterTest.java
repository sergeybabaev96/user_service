package school.faang.user_service.service.subscription.user.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.user.filter.CityFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class CityFilterTest {
    private CityFilter cityFilter;
    private UserFilterDto userFilterDto;
    private Stream<User> users;
    private User firstUser;
    private User secondUser;
    private User thirdUser;

    @BeforeEach
    void setup() {
        cityFilter = new CityFilter();

        userFilterDto = new UserFilterDto();

        firstUser = new User();
        firstUser.setCity("New York");

        secondUser = new User();
        secondUser.setCity("Toronto");

        thirdUser = new User();
        thirdUser.setCity("Kyiv");

        users = Stream.of(firstUser, secondUser, thirdUser);
    }

    @Test
    void shouldBeApplicableWhenCityPatternIsNotNull() {
        userFilterDto.setCityPattern("Toronto");
        boolean isApplicable = cityFilter.isApplicable(userFilterDto);

        assertTrue(isApplicable);
    }

    @Test
    void shouldNotBeApplicableWhenCityPatternIsNull() {
        userFilterDto.setCityPattern(null);
        boolean isApplicable = cityFilter.isApplicable(userFilterDto);

        assertFalse(isApplicable);
    }

    @Test
    void shouldFilterUsersByCityPattern() {
        userFilterDto.setCityPattern("Toronto");
        List<User> filteredUsers = cityFilter.apply(users, userFilterDto);

        assertEquals(1, filteredUsers.size());
        assertTrue(filteredUsers.contains(secondUser));
    }
}
