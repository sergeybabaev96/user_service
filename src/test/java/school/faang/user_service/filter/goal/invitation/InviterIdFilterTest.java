package school.faang.user_service.filter.goal.invitation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class InviterIdFilterTest extends InvitationFilterTest {

    @BeforeEach
    void setUp() {
        invitationFilter = new InviterIdFilter();
        filters = new InvitationFilterDto();
    }

    @Test
    void isApplicable_ShouldReturnTrueWhenIdIsNotNull() {
        filters.setInviterId(1L);

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
        filters.setInviterId(1L);

        invitation1 = new GoalInvitation();
        User inviter1 = new User();
        inviter1.setId(1L);
        invitation1.setInviter(inviter1);

        invitation2 = new GoalInvitation();
        User inviter2 = new User();
        inviter2.setId(2L);
        invitation2.setInviter(inviter2);

        invitation3 = new GoalInvitation();
        User inviter3 = new User();
        inviter3.setId(1L);
        invitation3.setInviter(inviter3);

        Stream<GoalInvitation> input = Stream.of(invitation1, invitation2, invitation3);
        Stream<GoalInvitation> expected = Stream.of(invitation1, invitation3);

        List<GoalInvitation> result = invitationFilter.apply(input, filters).toList();

        assertEquals(expected.toList(), result);
    }

    @Test
    void apply_ShouldReturnEmptyStreamWhenNoInvitationsMatch() {
        filters.setInviterId(3L);

        invitation1 = new GoalInvitation();
        User inviter1 = new User();
        inviter1.setId(1L);
        invitation1.setInviter(inviter1);

        invitation2 = new GoalInvitation();
        User inviter2 = new User();
        inviter2.setId(2L);
        invitation2.setInviter(inviter2);

        Stream<GoalInvitation> input = Stream.of(invitation1, invitation2);

        List<GoalInvitation> result = invitationFilter.apply(input, filters).toList();

        assertTrue(result.isEmpty());
    }
}