package school.faang.user_service.filter.goal.invitation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class StatusFilterTest extends InvitationFilterTest {

    @BeforeEach
    void setUp() {
        invitationFilter = new StatusFilter();
        filters = new InvitationFilterDto();
    }

    @Test
    void isApplicable_ShouldReturnTrueWhenStatusIsNotNull() {
        filters.setStatus(RequestStatus.PENDING);

        boolean result = invitationFilter.isApplicable(filters);

        assertTrue(result);
    }

    @Test
    void isApplicable_ShouldReturnFalseWhenStatusIsNull() {
        boolean result = invitationFilter.isApplicable(filters);

        assertFalse(result);
    }

    @Test
    void apply_ShouldFilterInvitationsByMatchingStatus() {
        filters.setStatus(RequestStatus.PENDING);

        invitation1 = new GoalInvitation();
        invitation1.setStatus(RequestStatus.PENDING);

        invitation2 = new GoalInvitation();
        invitation2.setStatus(RequestStatus.REJECTED);

        invitation3 = new GoalInvitation();
        invitation3.setStatus(RequestStatus.PENDING);

        Stream<GoalInvitation> input = Stream.of(invitation1, invitation2, invitation3);
        Stream<GoalInvitation> expected = Stream.of(invitation1, invitation3);

        List<GoalInvitation> result = invitationFilter.apply(input, filters).toList();

        assertEquals(expected.toList(), result);
    }

    @Test
    void apply_ShouldReturnEmptyStreamWhenNoInvitationsMatch() {
        filters.setStatus(RequestStatus.REJECTED);

        invitation1 = new GoalInvitation();
        invitation1.setStatus(RequestStatus.PENDING);

        invitation2 = new GoalInvitation();
        invitation2.setStatus(RequestStatus.ACCEPTED);

        Stream<GoalInvitation> input = Stream.of(invitation1, invitation2);

        List<GoalInvitation> result = invitationFilter.apply(input, filters).toList();

        assertTrue(result.isEmpty());
    }
}