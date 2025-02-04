package school.faang.user_service.service;

import org.springframework.http.ResponseEntity;
import school.faang.user_service.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;
import java.util.Objects;


public interface UserService {
    ResponseEntity<UserDto> getUser(long userId);
    private final UserRepository userRepository;

    public User deactivateUser(long userId) {
        // deactivation
        User user = Objects.requireNonNull(userRepository.findById(userId).orElse(null));
        user.setActive(false);
        userRepository.save(user);
        return user;
    }
}
