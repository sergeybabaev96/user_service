package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final EventService eventService;

    public boolean doesUserExist(long userId) {
        return userRepository.existsById(userId);
    }

    public User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new DataRetrievalFailureException(
                        "User with id %d is not found".formatted(userId)));
    }

    public UserDto deactivateUser(long userId) {
        eventService.deleteEventByUserId(userId);
        User user = getUserById(userId);

    }
}
