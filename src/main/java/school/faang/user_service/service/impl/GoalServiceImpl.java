package school.faang.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.GoalService;

import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {
    private final GoalRepository goalRepository;

    @Override
    @Transactional
    public void deactivateGoalsByUserId(long userId) {
        Stream<Long> goalIdStream = goalRepository.deleteUserGoalByUserId(userId);
        Stream<Long> mustBeDeletedGoalIds = goalIdStream
                .filter(goalId -> !goalRepository.existsOtherGoalsInProcess(goalId, userId)
                );
        goalRepository.deleteAllById(mustBeDeletedGoalIds.toList());
    }
}

