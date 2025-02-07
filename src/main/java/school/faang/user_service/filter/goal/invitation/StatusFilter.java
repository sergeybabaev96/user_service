package school.faang.user_service.filter.goal.invitation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.dto.goal.InvitationFilterDto;

import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class StatusFilter implements InvitationFilter {

    @Override
    public boolean isApplicable(InvitationFilterDto filter) {
        return filter.getStatus() != null;
    }

    @Override
    public Stream<GoalInvitation> apply(Stream<GoalInvitation> invitations, InvitationFilterDto filter) {
        return invitations.filter(invitation ->
                invitation.getStatus().equals(filter.getStatus()));
    }
}