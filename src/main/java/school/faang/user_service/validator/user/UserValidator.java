package school.faang.user_service.validator.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.UserRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserValidator {
    private final UserRepository userRepository;

    public void validateUser(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> {
            log.error("User with id {} not found", userId);
            return new EntityNotFoundException(String.format("There isn't user with id = %d", userId));
        });
    }
}
