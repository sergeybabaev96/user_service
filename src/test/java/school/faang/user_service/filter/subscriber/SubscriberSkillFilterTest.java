package school.faang.user_service.filter.subscriber;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.subscriber.SubscriberFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.Skill;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SubscriberSkillFilterTest {
    private Skill firstSkill1, secondSkill1, firstSkill2, firstSkill3;
    private User user1, user2, user3;
    private SubscriberSkillFilter filter;
    private SubscriberFilterDto filterDto;

    @BeforeEach
    void setUp() {
        filter = new SubscriberSkillFilter();
        filterDto = new SubscriberFilterDto();

        firstSkill1 = new Skill();
        secondSkill1 = new Skill();
        firstSkill2 = new Skill();
        firstSkill3 = new Skill();
        firstSkill1.setTitle("java advanced");
        secondSkill1.setTitle("friendly");
        firstSkill2.setTitle("java elementary");
        firstSkill3.setTitle("communication");

        user1 = new User();
        user2 = new User();
        user3 = new User();
        user1.setSkills(List.of(firstSkill1, secondSkill1));
        user2.setSkills(List.of(firstSkill2));
        user3.setSkills(List.of(firstSkill3));
    }

    @Test
    void testIsApplicableWhenSkillPatternIsSet() {
        filterDto.setSkillPattern("openness");
        assertTrue(filter.isApplicable(filterDto));
    }

    @Test
    void testIsApplicableWhenSkillPatternIsNull() {
        filterDto.setSkillPattern(null);
        assertFalse(filter.isApplicable(filterDto));
    }

    @Test
    void testApplyFilterMatchesUsersWithSkill() {
        filterDto.setSkillPattern("friendly");
        applyAndAssertCount(Stream.of(user1, user2, user3), filterDto, 1);
    }

    @Test
    void testApplyFilterMatchesMultipleUsers() {
        filterDto.setSkillPattern("java");
        applyAndAssertCount(Stream.of(user1, user2, user3), filterDto, 2);
    }

    @Test
    void testApplyNoMatchingUsers() {
        filterDto.setSkillPattern("Python");
        applyAndAssertCount(Stream.of(user1, user2, user3), filterDto, 0);
    }

    @Test
    void testApplyUserWithNullSkills() {
        user1.setSkills(null);
        filterDto.setSkillPattern("java");
        applyAndAssertCount(Stream.of(user1, user2), filterDto, 1);
    }

    @Test
    void testApplyUserWithSkillHavingNullTitle() {
        firstSkill2.setTitle(null);
        user2.setSkills(List.of(firstSkill2));
        filterDto.setSkillPattern("java");

        applyAndAssertCount(Stream.of(user1, user2), filterDto, 1);
    }

    private void applyAndAssertCount(Stream<User> users, SubscriberFilterDto filterDto, long expectedCount) {
        long actualCount = filter.apply(users, filterDto).count();
        assertEquals(expectedCount, actualCount);
    }
}