package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.filter.goal.GoalInvitationInvitedIdFilter;
import school.faang.user_service.filter.goal.GoalInvitationInviterIdFilter;
import school.faang.user_service.filter.goal.GoalInvitationStatusFilter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Stream;

public class GoalInvitationFilterTest {

    private final GoalInvitationInviterIdFilter inviterIdFilter = new GoalInvitationInviterIdFilter();
    private final GoalInvitationInvitedIdFilter invitedIdFilter = new GoalInvitationInvitedIdFilter();
    private final GoalInvitationStatusFilter statusFilter = new GoalInvitationStatusFilter();

    @Test
    void testIsApplicableWhenInviterIdIsNotNull() {
        InvitationFilterDto filterDto = new InvitationFilterDto(1L, null, null);
        assertTrue(inviterIdFilter.isApplicable(filterDto));
    }

    @Test
    void testIsApplicableWhenInviterIdIsNull() {
        InvitationFilterDto filterDto = new InvitationFilterDto(null, null, null);
        assertFalse(inviterIdFilter.isApplicable(filterDto));
    }

    @Test
    void testIsApplicableWhenInvitedIdIsNotNull() {
        InvitationFilterDto filterDto = new InvitationFilterDto(null, 1L, null);
        assertTrue(invitedIdFilter.isApplicable(filterDto));
    }

    @Test
    void testIsApplicableWhenInvitedIdIsNull() {
        InvitationFilterDto filterDto = new InvitationFilterDto(null, null, null);
        assertFalse(invitedIdFilter.isApplicable(filterDto));
    }

    @Test
    void testIsApplicableWhenStatusIsNotNull() {
        InvitationFilterDto filterDto = new InvitationFilterDto(null, null, RequestStatus.ACCEPTED);
        assertTrue(statusFilter.isApplicable(filterDto));
    }

    @Test
    void testIsApplicableWhenStatusIsNull() {
        InvitationFilterDto filterDto = new InvitationFilterDto(null, null, null);
        assertFalse(statusFilter.isApplicable(filterDto));
    }

    @Test
    void testApplyFilterByInviterId() {
        InvitationFilterDto filterDto = new InvitationFilterDto(1L, null, null);
        Stream<GoalInvitation> invitations = Stream.of(
                createGoalInvitation(1L, 2L, RequestStatus.ACCEPTED),
                createGoalInvitation(2L, 1L, RequestStatus.REJECTED)
        );
        List<GoalInvitation> result = inviterIdFilter.apply(invitations, filterDto).toList();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getInviter().getId());
    }

    @Test
    void testApplyFilterByInvitedId() {
        InvitationFilterDto filterDto = new InvitationFilterDto(null, 1L, null);
        Stream<GoalInvitation> invitations = Stream.of(
                createGoalInvitation(1L, 2L, RequestStatus.ACCEPTED),
                createGoalInvitation(2L, 1L, RequestStatus.REJECTED)
        );
        List<GoalInvitation> result = invitedIdFilter.apply(invitations, filterDto).toList();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getInvited().getId());
    }

    @Test
    void testApplyFilterByStatus() {
        InvitationFilterDto filterDto = new InvitationFilterDto(null, null, RequestStatus.ACCEPTED);
        Stream<GoalInvitation> invitations = Stream.of(
                createGoalInvitation(1L, 2L, RequestStatus.ACCEPTED),
                createGoalInvitation(2L, 1L, RequestStatus.REJECTED)
        );
        List<GoalInvitation> result = statusFilter.apply(invitations, filterDto).toList();

        assertEquals(1, result.size());
        assertEquals(RequestStatus.ACCEPTED, result.get(0).getStatus());
    }

    private GoalInvitation createGoalInvitation(Long firstUserId, Long secondUserId, RequestStatus status) {
        GoalInvitation goalInvitation = new GoalInvitation();
        goalInvitation.setInviter(createUser(firstUserId));
        goalInvitation.setInvited(createUser(secondUserId));
        goalInvitation.setStatus(status);
        return goalInvitation;
    }

    private User createUser(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }

}
