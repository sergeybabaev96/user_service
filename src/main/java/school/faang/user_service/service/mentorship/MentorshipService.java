package school.faang.user_service.service.mentorship;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class MentorshipService {
    private MentorshipRepository mentorshipRepository;

    /**
     * - deletes mentees from mentor
     * - changes mentee_id of goals created by mentor
     * to id of first user associated with the goal.
     * @param user - mentor who is being deactivated
     */
    public void stopMentorship(User user) {
        List<Goal> goalsControlledByUser = user.getSetGoals();
        for(Goal goal : goalsControlledByUser) {
            List<User> usersOfGoal = goal.getUsers();
            List<User> usersOfGoalWithoutDeactivatedUser = usersOfGoal.stream()
                    .filter(u -> !u.getId().equals(user.getId()))
                    .collect(Collectors.toList());
            goal.setMentor(usersOfGoalWithoutDeactivatedUser.get(0));
            log.debug("Goal \"{}\" has changed mentor_id: old Id={} , new Id=\"{}\"",
                    goal.getDescription(),
                    user.getId(),
                    usersOfGoalWithoutDeactivatedUser.get(0).getId());
        }
        user.getSetGoals().removeAll(goalsControlledByUser);

        mentorshipRepository.deleteByMentorId(user.getId());
        log.debug("Deleting mentees for mentor with mentor_id={}", user);
    }
}
