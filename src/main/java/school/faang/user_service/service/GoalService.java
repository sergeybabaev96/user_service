package school.faang.user_service.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;

    public void deleteUserFromGoals(Long userId) {
        List<Goal> goalsToUser = getActiveGoalsToUser(userId);
        goalsToUser.forEach(goal -> {
            List<User> usersWithoutDeactivatedUser = goal.getUsers().stream()
                    .filter(user -> !Objects.equals(user.getId(), userId)).toList();
            if (usersWithoutDeactivatedUser.isEmpty()) {
                goalRepository.delete(goal);
            } else {
                goal.setUsers(usersWithoutDeactivatedUser);
            }

        });
        List<Goal> goalsWithFollower  = goalsToUser.stream()
                .filter(goal -> !goal.getUsers().isEmpty()).toList();
        goalRepository.saveAll(goalsWithFollower);
    }

    public void setNullInGoalsToMentor(Long userId){
        List<Goal> goalsWhereUserMentor = goalRepository.findAllByMentorId(userId);
        goalsWhereUserMentor.stream()
                .peek(goal-> goal.setMentor(null)).toList();
        goalRepository.saveAll(goalsWhereUserMentor);
    }

    private List<Goal> getActiveGoalsToUser(Long userId) {
        return goalRepository.findGoalsByUserId(userId)
                .filter(goal-> goal.getStatus() == GoalStatus.ACTIVE).toList();
    }
}
