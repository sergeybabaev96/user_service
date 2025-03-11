package school.faang.user_service.service.goal.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class InvitationFilterRequestStatusTest {

    @InjectMocks
    private InvitationFilterRequestStatus invitationFilterRequestStatus;

    private GoalInvitation goalInvitation;
    private InvitationFilterDto filters;

    @BeforeEach
    void setUp() {
        goalInvitation = new GoalInvitation();
    }

    @Test
    void testIsAcceptableTrue() {
        filters = new InvitationFilterDto(null, null, null, null, RequestStatus.ACCEPTED);
        assertTrue(invitationFilterRequestStatus.isAcceptable(filters));
    }

    @Test
    void testIsAcceptableFalse() {
        filters = new InvitationFilterDto(null, null, null, null, null);
        assertFalse(invitationFilterRequestStatus.isAcceptable(filters));
    }

    @Test
    void testApplyFilterWork() {
        filters = new InvitationFilterDto(null, null, null, null, RequestStatus.ACCEPTED);
        goalInvitation.setStatus(RequestStatus.ACCEPTED);

        Stream<GoalInvitation> streamGoalInvitation = Stream.of(goalInvitation);
        Stream<GoalInvitation> streamApplyFilter = invitationFilterRequestStatus.apply(streamGoalInvitation, filters);
        assertEquals(1, streamApplyFilter.toList().size());
    }

    @Test
    void testApplyFilterUnequal() {
        filters = new InvitationFilterDto(null, null, null, null, RequestStatus.ACCEPTED);
        goalInvitation.setStatus(RequestStatus.REJECTED);

        Stream<GoalInvitation> streamGoalInvitation = Stream.of(goalInvitation);
        Stream<GoalInvitation> streamApplyFilter = invitationFilterRequestStatus.apply(streamGoalInvitation, filters);
        assertEquals(0, streamApplyFilter.toList().size());
    }

    @Test
    void testApplyFilterInvitationIsNull() {
        Stream<GoalInvitation> streamGoalInvitation = Stream.of(goalInvitation);
        Stream<GoalInvitation> streamApplyFilter = invitationFilterRequestStatus.apply(streamGoalInvitation, filters);
        assertEquals(0, streamApplyFilter.toList().size());
    }
}