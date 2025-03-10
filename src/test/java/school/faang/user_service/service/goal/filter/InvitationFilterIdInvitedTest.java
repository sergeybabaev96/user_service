package school.faang.user_service.service.goal.filter;

import lombok.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class InvitationFilterIdInvitedTest {

    public static final long INVITED_ID = 1L;
    @InjectMocks
    private InvitationFilterIdInvited invitationFilterIdInvited;

    private GoalInvitation goalInvitation;
    private InvitationFilterDto filters;
    private User user;

    @BeforeEach
    void setUp() {
        goalInvitation = new GoalInvitation();
        user = new User();
    }

    @Test
    void testIsAcceptableTrue() {
        filters = new InvitationFilterDto(null, null, null, 1L, null);
        assertTrue(invitationFilterIdInvited.isAcceptable(filters));
    }

    @Test
    void testIsAcceptableFalse() {
        filters = new InvitationFilterDto(null, null, null, null, null);
        assertFalse(invitationFilterIdInvited.isAcceptable(filters));
    }

    @Test
    void testApplyFilterWork() {
        filters = new InvitationFilterDto(null, null, null, INVITED_ID, null);
        fillParamsForApplayingFilter(1L, user);

        Stream<GoalInvitation> streamGoalInvitation = Stream.of(goalInvitation);
        Stream<GoalInvitation> streamApplyFilter = invitationFilterIdInvited.apply(streamGoalInvitation, filters);
        assertEquals(1, streamApplyFilter.toList().size());
    }

    @Test
    void testApplyFilterUnequal() {
        fillParamsForApplayingFilter(2L, user);

        filters = new InvitationFilterDto(null, null, null, INVITED_ID, null);

        Stream<GoalInvitation> streamGoalInvitation = Stream.of(goalInvitation);
        Stream<GoalInvitation> streamApplyFilter = invitationFilterIdInvited.apply(streamGoalInvitation, filters);
        assertEquals(0, streamApplyFilter.toList().size());
    }

    @Test
    void testApplyFilterInvitedIsNull() {
        filters = new InvitationFilterDto(null, null, null, INVITED_ID, null);
        goalInvitation.setInvited(null);

        Stream<GoalInvitation> streamGoalInvitation = Stream.of(goalInvitation);
        Stream<GoalInvitation> streamApplyFilter = invitationFilterIdInvited.apply(streamGoalInvitation, filters);
        assertEquals(0, streamApplyFilter.toList().size());
    }

    private void fillParamsForApplayingFilter(long userId, @NonNull User user) {
        user.setId(userId);
        goalInvitation.setInvited(user);
    }
}