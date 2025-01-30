package school.faang.user_service.filter.goal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.InvitationFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.filter.Filter;
import school.faang.user_service.service.UserService;

import java.util.stream.Stream;

@Component
public class InvitedNamePatternFilter implements Filter<GoalInvitation, InvitationFilterDto> {

    private final UserService userService;

    @Autowired
    public InvitedNamePatternFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean isApplicable(InvitationFilterDto filter) {
        return filter.getInvitedNamePattern() != null;
    }

    @Override
    public void apply(Stream<GoalInvitation> goalInvitation, InvitationFilterDto filter) {
        goalInvitation.filter(invitation -> invitation.getInvited().getUsername().contains(
                userService.allUsersStream()
                        .map(User::getUsername)
                        .filter(username -> username.equals(filter.getInvitedNamePattern())).toString()));
    }
}
