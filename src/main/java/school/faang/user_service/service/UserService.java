package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public boolean existsById(long userId) {
        return userRepository.existsById(userId);
    }

    public User getReferenceById(long userId) {
        if (!userRepository.existsById(userId))
            throw new EntityNotFoundException("User not founds");
        return userRepository.getReferenceById(userId);
    }
}
