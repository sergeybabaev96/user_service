package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventOwnerImpl implements EventOwner {
    private final UserRepository userRepository;

    @Override
    public User getOwner(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.orElseThrow(() ->
                        new DataValidationException("User with id = %d not found".formatted(id)));
    }
}
