package school.faang.user_service.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
public class GoalService {
    private final GoalRepository repository;

    public Optional<Goal> findById(Long id) {
        return repository.findById(id);
    }
}
