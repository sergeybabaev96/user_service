package school.faang.user_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.exception.GoalFoundForUserException;
import school.faang.user_service.kafka.KafkaProducer;
import school.faang.user_service.kafka.event.GoalSetEvent;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.GoalService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;
    private final KafkaProducer kafkaProducerImpl;

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

    @Override
    @Transactional
    public void saveGoalForUser(long userId, long goalId) {
        boolean isGoalDistinct = goalRepository
                .findGoalsByUserId(userId)
                .anyMatch(goal -> goal.getId() == goalId);
        if (isGoalDistinct) {
            throw new GoalFoundForUserException("same goal was found for this user");
        }
        goalRepository.saveGoalForUser(userId, goalId);
        GoalSetEvent goalSetEvent = new GoalSetEvent(userId, goalId);
        kafkaProducerImpl.produceToAchievementService(goalSetEvent);
    }


}

