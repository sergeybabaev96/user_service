package school.faang.user_service.service.mentorship;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class MentorshipService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GoalRepository goalRepository;

    /**
     * - deletes mentees from mentor
     * - changes mentee_id of goals created by mentor
     * to id of first user associated with the goal.
     * @param userToDeactivate - mentor who is being deactivated
     */
    public void stopMentorship(User userToDeactivate) {
        List<Goal> goalsControlledByMentor = goalRepository.findGoalsByMentorId(userToDeactivate.getId());
        for(Goal goal : goalsControlledByMentor) {
            List<User> usersOfGoal = goalRepository.findUsersByGoalIdHql(goal.getId());
            goal.setMentor(usersOfGoal.get(0));
            log.debug("Goal \"{}\" has changed mentor_id: old Id={} , new Id=\"{}\"", goal.getDescription(), userToDeactivate.getId(), usersOfGoal.get(0).getId() );
        }
        goalRepository.saveAll(goalsControlledByMentor);

        List<User> mentees = new ArrayList<>();
        userToDeactivate.setMentees(mentees);
        userRepository.save(userToDeactivate);
        log.debug("Mentor {} mentees number after deactivation={}",userToDeactivate.getUsername(), userToDeactivate.getMentees().size());
    }
}
