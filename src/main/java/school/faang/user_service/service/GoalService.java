package school.faang.user_service.service;


import java.util.stream.Stream;

public interface GoalService {

    void deactivateGoalsByUserId(long userId);
}
