package school.faang.user_service.service.goal.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.stream.Stream;

@Component
public class InvitationFilterNameInviter implements InvitationFilter {

    @Override
    public boolean isAcceptable(InvitationFilterDto filters) {
        return filters.inviterNamePattern() != null;
    }

    @Override
    public Stream<GoalInvitation> apply(Stream<GoalInvitation> goalInvitation, InvitationFilterDto filters) {
        return goalInvitation.filter(invitation -> invitation.getInviter() != null
                && invitation.getInviter().getUsername() != null
                && invitation.getInviter().getUsername().contains(filters.inviterNamePattern()));

    }
}
