package school.faang.user_service.service;

public interface GoalService {

    boolean existsById(Long goalId);

    long countActiveGoalsPerUser(Long userId);

    void deleteUserFromGoals(Long userId);

}
