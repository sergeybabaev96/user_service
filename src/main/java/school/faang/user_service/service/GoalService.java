package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalDto;
import school.faang.user_service.entity.goal.mapper.GoalMapperImpl;
import school.faang.user_service.exception.goal.MaxActiveGoalPerUserException;
import school.faang.user_service.repository.goal.GoalRepository;

@Service
@RequiredArgsConstructor
public class GoalService {

    @Value("${goal.maxLimit}")
    private Integer goalLimit;
    private final GoalRepository goalRepository;
    private final GoalMapperImpl goalMapper;
    private final SkillService skillService;

    @Transactional
    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        boolean isUserReachActiveGoalLimit = goalRepository.countActiveGoalsPerUser(userId) >= goalLimit;
        if (isUserReachActiveGoalLimit) {
            throw new MaxActiveGoalPerUserException(userId, goalLimit);
        }
        Goal goal = goalRepository.create(goalDto.title(), goalDto.description(), goalDto.parent());
        goalRepository.assignGoalToUser(userId, goal.getId());
        if (!goalDto.skillsId().isEmpty()) {
            skillService.assignSkillToGoal(goal.getId(), goalDto.skillsId());
        }
        return goalMapper.goalToDto(goal);
    }
}