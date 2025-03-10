package school.faang.user_service.service.subscription.user.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.user.filter.NameFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class NameFilterTest {
    private NameFilter nameFilter;
    private UserFilterDto userFilterDto;
    private Stream<User> users;
    private User firstUser;
    private User secondUser;
    private User thirdUser;

    @BeforeEach
    void setup() {
        nameFilter = new NameFilter();

        userFilterDto = new UserFilterDto();
        userFilterDto.setNamePattern("John");

        firstUser = new User();
        firstUser.setUsername("JohnDoe");

        secondUser = new User();
        secondUser.setUsername("JaneSmith");

        thirdUser = new User();
        thirdUser.setUsername("Michael");

        users = Stream.of(firstUser, secondUser, thirdUser);
    }

    @Test
    void shouldBeApplicableWhenNamePatternIsNotNull() {
        boolean isApplicable = nameFilter.isApplicable(userFilterDto);

        assertTrue(isApplicable);
    }

    @Test
    void shouldNotBeApplicableWhenNamePatternIsNull() {
        userFilterDto.setNamePattern(null);
        boolean isApplicable = nameFilter.isApplicable(userFilterDto);

        assertFalse(isApplicable);
    }

    @Test
    void shouldFilterUsersByNamePattern() {
        userFilterDto.setNamePattern("John");
        List<User> filteredUsers = nameFilter.apply(users, userFilterDto);

        assertEquals(1, filteredUsers.size());
        assertTrue(filteredUsers.contains(firstUser));
    }
}
