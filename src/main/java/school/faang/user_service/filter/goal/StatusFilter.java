package school.faang.user_service.filter.goal;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.filter.Filter;

import java.util.stream.Stream;

@Component
public class StatusFilter implements Filter<GoalInvitation, InvitationFilterDto> {
    @Override
    public boolean isApplicable(InvitationFilterDto filter) {
        return filter.getStatus() != null;
    }

    @Override
    public void apply(Stream<GoalInvitation> goalInvitation, InvitationFilterDto filter) {
        goalInvitation.filter(invitation -> invitation.getStatus().equals(filter.getStatus()));
    }
}
