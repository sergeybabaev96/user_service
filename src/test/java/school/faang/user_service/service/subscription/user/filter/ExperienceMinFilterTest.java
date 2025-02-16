package school.faang.user_service.service.subscription.user.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.user.filter.ExperienceMinFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExperienceMinFilterTest {
    private ExperienceMinFilter experienceMinFilter;
    private UserFilterDto userFilterDto;
    private Stream<User> users;
    private User firstUser;
    private User secondUser;
    private User thirdUser;

    @BeforeEach
    void setup() {
        experienceMinFilter = new ExperienceMinFilter();

        userFilterDto = new UserFilterDto();
        userFilterDto.setExperienceMin(5);

        firstUser = new User();
        firstUser.setExperience(1);

        secondUser = new User();
        secondUser.setExperience(4);

        thirdUser = new User();
        thirdUser.setExperience(6);

        users = Stream.of(firstUser, secondUser, thirdUser);
    }

    @Test
    void shouldBeApplicableWhenExperienceMinIsNotZero() {
        boolean isApplicable = experienceMinFilter.isApplicable(userFilterDto);

        assertTrue(isApplicable);
    }

    @Test
    void shouldNotBeApplicableWhenExperienceMinIsZero() {
        userFilterDto.setExperienceMin(0);
        boolean isApplicable = experienceMinFilter.isApplicable(userFilterDto);

        assertFalse(isApplicable);
    }

    @Test
    void shouldFilterUsersByExperienceMin() {
        userFilterDto.setExperienceMin(5);
        List<User> filteredUsers = experienceMinFilter.apply(users, userFilterDto);

        assertEquals(1, filteredUsers.size());
        assertTrue(filteredUsers.contains(thirdUser));
    }
}
