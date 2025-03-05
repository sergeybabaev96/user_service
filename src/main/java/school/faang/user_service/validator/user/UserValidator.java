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

    public void validateUser(Long id) {
        log.info("Validating user with id: {}", id);
        if (!userRepository.existsById(id)) {
            log.error("User with id {} not found", id);
            throw new EntityNotFoundException("User not found");
        }
        log.info("User with id {} validated successfully", id);
    }
}
