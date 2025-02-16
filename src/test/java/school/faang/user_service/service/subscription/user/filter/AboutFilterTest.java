package school.faang.user_service.service.subscription.user.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.user.filter.AboutFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class AboutFilterTest {
    private AboutFilter aboutFilter;
    private UserFilterDto userFilterDto;
    private Stream<User> users;
    private User firstUser;
    private User secondUser;
    private User thirdUser;

    @BeforeEach
    void setup() {
        aboutFilter = new AboutFilter();

        userFilterDto = new UserFilterDto();
        userFilterDto.setAboutPattern("Developer");

        firstUser = new User();
        firstUser.setAboutMe("Java Developer with 5 years of experience");

        secondUser = new User();
        secondUser.setAboutMe("Python Developer with 3 years of experience");

        thirdUser = new User();
        thirdUser.setAboutMe("Data Scientist");

        users = Stream.of(firstUser, secondUser, thirdUser);
    }

    @Test
    void shouldBeApplicableWhenAboutPatternIsNotNull() {
        boolean isApplicable = aboutFilter.isApplicable(userFilterDto);

        assertTrue(isApplicable);
    }

    @Test
    void shouldNotBeApplicableWhenAboutPatternIsNull() {
        userFilterDto.setAboutPattern(null);
        boolean isApplicable = aboutFilter.isApplicable(userFilterDto);

        assertFalse(isApplicable);
    }

    @Test
    void shouldFilterUsersByAboutPattern() {
        userFilterDto.setAboutPattern("developer");
        List<User> filteredUsers = aboutFilter.apply(users, userFilterDto);

        assertEquals(2, filteredUsers.size());
        assertTrue(filteredUsers.contains(firstUser));
        assertTrue(filteredUsers.contains(secondUser));
        assertFalse(filteredUsers.contains(thirdUser));
    }
}
