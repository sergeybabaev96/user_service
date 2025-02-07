package school.faang.user_service.filter.goal.invitation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class InvitedNameFilterTest {

    private InvitedNameFilter invitedNameFilter;
    private InvitationFilterDto filters;

    @BeforeEach
    void setUp() {
        invitedNameFilter = new InvitedNameFilter();
        filters = new InvitationFilterDto();
    }

    @Test
    void isApplicable_ShouldReturnTrueWhenNamePatternIsNotNull() {
        filters.setInvitedNamePattern("John");

        boolean result = invitedNameFilter.isApplicable(filters);

        assertTrue(result);
    }

    @Test
    void isApplicable_ShouldReturnFalseWhenNamePatternIsNull() {
        filters.setInvitedNamePattern(null);

        boolean result = invitedNameFilter.isApplicable(filters);

        assertFalse(result);
    }

    @Test
    void apply_ShouldFilterInvitationsByMatchingNamePattern() {
        filters.setInvitedNamePattern("John");

        GoalInvitation invitation1 = new GoalInvitation();
        User invited1 = new User();
        invited1.setUsername("John Smith");
        invitation1.setInvited(invited1);

        GoalInvitation invitation2 = new GoalInvitation();
        User invited2 = new User();
        invited2.setUsername("Jane Doe");
        invitation2.setInvited(invited2);

        GoalInvitation invitation3 = new GoalInvitation();
        User invited3 = new User();
        invited3.setUsername("Johnathan Doe");
        invitation3.setInvited(invited3);

        Stream<GoalInvitation> input = Stream.of(invitation1, invitation2, invitation3);
        List<GoalInvitation> result = invitedNameFilter.apply(input, filters).toList();

        assertEquals(2, result.size());
        assertTrue(result.contains(invitation1));
        assertTrue(result.contains(invitation3));
    }

    @Test
    void apply_ShouldReturnEmptyStreamWhenNoInvitationsMatch() {
        filters.setInvitedNamePattern("Nonexistent");

        GoalInvitation invitation1 = new GoalInvitation();
        User invited1 = new User();
        invited1.setUsername("John Smith");
        invitation1.setInvited(invited1);

        GoalInvitation invitation2 = new GoalInvitation();
        User invited2 = new User();
        invited2.setUsername("Jane Doe");
        invitation2.setInvited(invited2);

        Stream<GoalInvitation> input = Stream.of(invitation1, invitation2);
        List<GoalInvitation> result = invitedNameFilter.apply(input, filters).toList();

        assertTrue(result.isEmpty());
    }
}