package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final int GOALS_PER_USER = 3;

    private final UserRepository userRepository;

    public boolean isWithinGoalLimit(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new DataValidationException("user not found"));
        int countGoals = user.getGoals().size();
        return countGoals <= GOALS_PER_USER;
    }
}

