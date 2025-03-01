package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.filter.GoalInvitationFilter;

import java.util.stream.Stream;

@Component
public class InvitedIdFilter implements GoalInvitationFilter {

    @Override
    public boolean isApplicable(InvitationFilterDto filter) {
        return filter.getInvitedId() != null;
    }

    @Override
    public Stream<GoalInvitation> apply(Stream<GoalInvitation> goalInvitation, InvitationFilterDto filter) {
        return goalInvitation.filter(invitation -> invitation.getInvited().getId().equals(filter.getInvitedId()));
    }
}
