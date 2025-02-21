package school.faang.user_service.service.mentorship;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.List;

@Service
@Slf4j
public class MentorshipService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private MentorshipRepository mentorshipRepository;

    /**
     * - deletes mentees from mentor
     * - changes mentee_id of goals created by mentor
     * to id of first user associated with the goal.
     *
     * @param userToDeactivateId - mentor who is being deactivated
     */
    public void stopMentorship(Long userToDeactivateId) {
        List<Goal> goalsControlledByMentor = goalRepository.findGoalsByMentorId(userToDeactivateId);
        for (Goal goal : goalsControlledByMentor) {
            List<User> usersOfGoal = goalRepository.findUsersByGoalIdHql(goal.getId());
            goal.setMentor(usersOfGoal.get(0));
            log.debug("Goal \"{}\" has changed mentor_id: old Id={} , new Id=\"{}\"", goal.getDescription(), userToDeactivateId, usersOfGoal.get(0).getId());
        }
        goalRepository.saveAll(goalsControlledByMentor);

        mentorshipRepository.deleteByMentorId(userToDeactivateId);
        log.debug("Deleting mentees for mentor with mentor_id={}", userToDeactivateId);
    }
}
