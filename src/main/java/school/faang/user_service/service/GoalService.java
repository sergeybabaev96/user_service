package school.faang.user_service.service;



public interface GoalService {

    void deactivateGoalsByUserId(long userId);

    void saveGoalForUser(long userId, long goalId);
}
