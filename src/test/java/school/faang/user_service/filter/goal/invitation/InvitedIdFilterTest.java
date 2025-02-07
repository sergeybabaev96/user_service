package school.faang.user_service.filter.goal.invitation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class InvitedIdFilterTest extends InvitationFilterTest {

    @BeforeEach
    void setUp() {
        invitationFilter = new InvitedIdFilter();
        filters = new InvitationFilterDto();
    }

    @Test
    void isApplicable_ShouldReturnTrueWhenIdIsNotNull() {
        filters.setInvitedId(1L);

        boolean result = invitationFilter.isApplicable(filters);

        assertTrue(result);
    }

    @Test
    void isApplicable_ShouldReturnFalseWhenIdIsNull() {
        boolean result = invitationFilter.isApplicable(filters);

        assertFalse(result);
    }

    @Test
    void apply_ShouldFilterInvitationsByMatchingId() {
        filters.setInvitedId(1L);

        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);
        User user3 = new User();
        user3.setId(1L);

        invitation1 = new GoalInvitation();
        invitation1.setInvited(user1);

        invitation2 = new GoalInvitation();
        invitation2.setInvited(user2);

        invitation3 = new GoalInvitation();
        invitation3.setInvited(user3);

        Stream<GoalInvitation> input = Stream.of(invitation1, invitation2, invitation3);
        Stream<GoalInvitation> expected = Stream.of(invitation1, invitation3);

        List<GoalInvitation> result = invitationFilter.apply(input, filters).toList();

        assertEquals(expected.toList(), result);
    }

    @Test
    void apply_ShouldReturnEmptyStreamWhenNoInvitationsMatch() {
        filters.setInvitedId(3L);

        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);

        invitation1 = new GoalInvitation();
        invitation1.setInvited(user1);

        invitation2 = new GoalInvitation();
        invitation2.setInvited(user2);

        Stream<GoalInvitation> input = Stream.of(invitation1, invitation2);

        List<GoalInvitation> result = invitationFilter.apply(input, filters).toList();

        assertTrue(result.isEmpty());
    }
}