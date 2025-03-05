package school.faang.user_service.validation.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.validation.ExceptionHandling;

@Component
@RequiredArgsConstructor
public class UserValidation {
    private final UserRepository userRepository;
    private final ExceptionHandling exceptionHandling;

    public void validateByExistsUserOnId(Long userId) {
        try {
            if (!userRepository.existsById(userId)) {
                throw new EntityNotFoundException("User not found");
            }
        } catch (EntityNotFoundException e) {
            exceptionHandling.handleException(e);
        }
    }
}
