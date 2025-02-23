package school.faang.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.GoalService;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {
    private final GoalRepository goalRepository;

    @Override
    @Transactional
    public void deactivateGoalsByUserId(long userId) {
        List<Long> mustBeDeletedGoalIds = goalRepository
                .deleteUserGoalByUserId(userId)
                .filter(
                    goalId -> !goalRepository.existsOtherGoalsInProcess(goalId, userId)
                )
                .toList();
        goalRepository.deleteAllById(mustBeDeletedGoalIds);
    }
}

