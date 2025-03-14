package school.faang.user_service.service.event;

import lombok.Data;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

@Service
@Data
public class EventOwner {
    private final UserRepository userRepository;

    public User getOwner(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new DataValidationException("User not found");
        }
        return user.get();
    }
}
