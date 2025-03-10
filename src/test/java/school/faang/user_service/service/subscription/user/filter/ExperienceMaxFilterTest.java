package school.faang.user_service.service.subscription.user.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.user.filter.ExperienceMaxFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class ExperienceMaxFilterTest {
    private ExperienceMaxFilter experienceMaxFilter;
    private UserFilterDto userFilterDto;
    private Stream<User> users;
    private User firstUser;
    private User secondUser;
    private User thirdUser;

    @BeforeEach
    void setup() {
        experienceMaxFilter = new ExperienceMaxFilter();

        userFilterDto = new UserFilterDto();
        userFilterDto.setExperienceMax(5);

        firstUser = new User();
        firstUser.setExperience(1);

        secondUser = new User();
        secondUser.setExperience(4);

        thirdUser = new User();
        thirdUser.setExperience(6);

        users = Stream.of(firstUser, secondUser, thirdUser);
    }

    @Test
    void shouldBeApplicableWhenExperienceMaxIsNotZero() {
        boolean isApplicable = experienceMaxFilter.isApplicable(userFilterDto);

        assertTrue(isApplicable);
    }

    @Test
    void shouldNotBeApplicableWhenExperienceMaxIsZero() {
        userFilterDto.setExperienceMax(0);
        boolean isApplicable = experienceMaxFilter.isApplicable(userFilterDto);

        assertFalse(isApplicable);
    }

    @Test
    void shouldFilterUsersByExperienceMax() {
        userFilterDto.setExperienceMax(5);
        List<User> filteredUsers = experienceMaxFilter.apply(users, userFilterDto);

        assertEquals(2, filteredUsers.size());
        assertTrue(filteredUsers.contains(firstUser));
        assertTrue(filteredUsers.contains(secondUser));
    }
}
