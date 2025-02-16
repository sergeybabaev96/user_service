package school.faang.user_service.service.subscription.user.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.user.filter.SkillFilter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SkillFilterTest {
    private SkillFilter skillFilter;
    private UserFilterDto userFilterDto;
    private Stream<User> users;
    private User firstUser;
    private User secondUser;
    private User thirdUser;

    @BeforeEach
    void setup() {
        skillFilter = new SkillFilter();

        userFilterDto = new UserFilterDto();
        Skill firstSill = new Skill();
        firstSill.setTitle("Java");

        Skill secondSkill = new Skill();
        secondSkill.setTitle("JavaScript");


        firstUser = new User();
        firstUser.setSkills(List.of(firstSill));

        secondUser = new User();
        secondUser.setSkills(List.of(firstSill));

        thirdUser = new User();
        thirdUser.setSkills(List.of(firstSill, secondSkill));

        users = Stream.of(firstUser, secondUser, thirdUser);
    }

    @Test
    void shouldBeApplicableWhenSkillPatternIsNotNull() {
        userFilterDto.setSkillPattern("java");
        boolean isApplicable = skillFilter.isApplicable(userFilterDto);

        assertTrue(isApplicable);
    }

    @Test
    void shouldNotBeApplicableWhenSkillPatternIsNull() {
        userFilterDto.setSkillPattern(null);
        boolean isApplicable = skillFilter.isApplicable(userFilterDto);

        assertFalse(isApplicable);
    }

    @Test
    void shouldFilterUsersBySkillPattern() {
        userFilterDto.setSkillPattern("java");
        List<User> filteredUsers = skillFilter.apply(users, userFilterDto);

        assertEquals(3, filteredUsers.size());
        assertTrue(filteredUsers.contains(firstUser));
        assertTrue(filteredUsers.contains(secondUser));
        assertTrue(filteredUsers.contains(thirdUser));
    }
}
