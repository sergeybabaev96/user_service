package school.faang.user_service.filter.goal.invitation;

import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.dto.goal.InvitationFilterDto;

import java.util.stream.Stream;

public interface InvitationFilter {
    boolean isApplicable(InvitationFilterDto filter);

    Stream<GoalInvitation> apply(Stream<GoalInvitation> invitations, InvitationFilterDto filter);
}