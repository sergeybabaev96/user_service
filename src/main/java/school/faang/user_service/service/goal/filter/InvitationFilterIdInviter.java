package school.faang.user_service.service.goal.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

@Component
public class InvitationFilterIdInviter implements InvitationFilter {

    @Override
    public boolean isAcceptable(InvitationFilterDto filters) {
        return filters.inviterId() != null;
    }

    @Override
    public Stream<GoalInvitation> apply(Stream<GoalInvitation> goalInvitation, InvitationFilterDto filters) {
        return goalInvitation.filter(invitation -> invitation.getInviter() != null
                && invitation.getInviter().getId() != null
                && invitation.getInviter().getId().equals(filters.inviterId()));
    }
}
