package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final int GOALS_PER_USER = 3;

    private final UserRepository userRepository;

    public boolean isWithinGoalLimit(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("User not found"));
        int countGoals = user.getGoals().size();
        return countGoals < GOALS_PER_USER;
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public Optional<User> findById(long id) {
        return userRepository.findById(id);
    }

    public UserDto findUserDtoById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("User not found"));
        return convertToDto(user);
    }

    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());

        dto.setEmail(user.getEmail());
        // Добавьте другие поля по необходимости
        return dto;
    }
}