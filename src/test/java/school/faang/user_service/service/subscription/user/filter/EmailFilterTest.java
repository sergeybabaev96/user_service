package school.faang.user_service.service.subscription.user.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.user.filter.EmailFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class EmailFilterTest {
    private EmailFilter emailFilter;
    private UserFilterDto userFilterDto;
    private Stream<User> users;
    private User firstUser;
    private User secondUser;
    private User thirdUser;

    @BeforeEach
    void setup() {
        emailFilter = new EmailFilter();

        userFilterDto = new UserFilterDto();
        userFilterDto.setEmailPattern("test1");

        firstUser = new User();
        firstUser.setEmail("test1@test.com");

        secondUser = new User();
        secondUser.setEmail("user2@test.com");

        thirdUser = new User();
        thirdUser.setEmail("mentor@email.com");

        users = Stream.of(firstUser, secondUser, thirdUser);
    }

    @Test
    void shouldBeApplicableWhenEmailPatternIsNotNull() {
        boolean isApplicable = emailFilter.isApplicable(userFilterDto);

        assertTrue(isApplicable);
    }

    @Test
    void shouldNotBeApplicableWhenEmailPatternIsNull() {
        userFilterDto.setEmailPattern(null);
        boolean isApplicable = emailFilter.isApplicable(userFilterDto);

        assertFalse(isApplicable);
    }

    @Test
    void shouldFilterUsersByEmailPattern() {
        userFilterDto.setEmailPattern("ment");
        List<User> filteredUsers = emailFilter.apply(users, userFilterDto);

        assertEquals(1, filteredUsers.size());
        assertTrue(filteredUsers.contains(thirdUser));
    }
}
