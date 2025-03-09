package school.faang.user_service.filter.goal;

import school.faang.user_service.entity.goal.GoalInvitation;

import java.util.List;
import java.util.function.Predicate;

public class GoalInvitationFilter {

    public static List<GoalInvitation> filter(List<GoalInvitation> goalInvitations, List<Predicate<GoalInvitation>> filters) {
        return goalInvitations.stream()
                .filter(goalInvitation -> filters.stream().allMatch(filter -> filter.test(goalInvitation)))
                .toList();
    }
}
