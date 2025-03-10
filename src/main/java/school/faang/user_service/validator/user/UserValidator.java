package school.faang.user_service.validator.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.UserRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserValidator {

    private final UserRepository userRepository;

    public void checkUserExistsById(Long userId) {
        if (!userRepository.existsById(userId)) {
            log.warn("User with id {} not found", userId);
            throw new EntityNotFoundException("User not found");
        }
    }
}
