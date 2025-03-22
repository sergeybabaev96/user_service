package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.repository.goal.GoalRepository;

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
}