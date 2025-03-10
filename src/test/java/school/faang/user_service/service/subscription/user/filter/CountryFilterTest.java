package school.faang.user_service.service.subscription.user.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.Country;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.user.filter.CountryFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CountryFilterTest {
    private CountryFilter countryFilter;
    private UserFilterDto userFilterDto;
    private Stream<User> users;
    private User firstUser;
    private User secondUser;
    private User thirdUser;

    @BeforeEach
    void setup() {
        countryFilter = new CountryFilter();

        userFilterDto = new UserFilterDto();
        Country firstCountry = new Country();
        firstCountry.setTitle("USA");

        Country secondCountry = new Country();
        secondCountry.setTitle("Canada");


        firstUser = new User();
        firstUser.setCountry(firstCountry);

        secondUser = new User();
        secondUser.setCountry(firstCountry);

        thirdUser = new User();
        thirdUser.setCountry(secondCountry);

        users = Stream.of(firstUser, secondUser, thirdUser);
    }

    @Test
    void shouldBeApplicableWhenCountryPatternIsNotNull() {
        userFilterDto.setCountryPattern("Canada");
        boolean isApplicable = countryFilter.isApplicable(userFilterDto);

        assertTrue(isApplicable);
    }

    @Test
    void shouldNotBeApplicableWhenCountryPatternIsNull() {
        userFilterDto.setCountryPattern(null);
        boolean isApplicable = countryFilter.isApplicable(userFilterDto);

        assertFalse(isApplicable);
    }

    @Test
    void shouldFilterUsersByCountryPattern() {
        userFilterDto.setCountryPattern("Canada");
        List<User> filteredUsers = countryFilter.apply(users, userFilterDto);

        assertEquals(1, filteredUsers.size());
        assertTrue(filteredUsers.contains(thirdUser));
    }
}
