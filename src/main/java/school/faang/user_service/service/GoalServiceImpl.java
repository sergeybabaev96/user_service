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
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;

    @Override
    public boolean existsById(Long goalId) {
        return goalRepository.existsById(goalId);
    }

    @Override
    public long countActiveGoalsPerUser(Long userId) {
        return goalRepository.countActiveGoalsPerUser(userId);
    }

    @Override
    public void deleteUserFromGoals(Long userId) {
        List<Goal> goalsToUser = getActiveGoalsToUser(userId);
        goalsToUser.forEach(goal -> {
            List<User> usersWithoutDeactivatedUser = goal.getUsers().stream()
                    .filter(user -> !Objects.equals(user.getId(), userId)).toList();
            if (usersWithoutDeactivatedUser.isEmpty()) {
                goalRepository.delete(goal);
                goal.setUsers(usersWithoutDeactivatedUser);
            } else {
                goal.setUsers(usersWithoutDeactivatedUser);
            }
        });
        List<Goal> goalsWithFollower = goalsToUser.stream()
                .filter(goal -> !goal.getUsers().isEmpty()).toList();
        goalRepository.saveAll(goalsWithFollower);
    }

    @Override
    public void deleteMentorFromGoals(Long userId) {
        List<Goal> goalsToMentor = getActiveGoalsToMentor(userId);
        goalsToMentor.forEach(goal -> goal.setMentor(null));
        goalRepository.saveAll(goalsToMentor);
    }

    private List<Goal> getActiveGoalsToUser(Long userId) {
        return goalRepository.findGoalsByUserId(userId)
                .filter(goal -> goal.getStatus() == GoalStatus.ACTIVE).toList();
    }

    private List<Goal> getActiveGoalsToMentor(Long userId) {
        return goalRepository.findAllByMentorId(userId).stream()
                .filter(goal -> goal.getStatus() == GoalStatus.ACTIVE).toList();
    }
}