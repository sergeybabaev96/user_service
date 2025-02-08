package school.faang.user_service.service.user;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.SubscriptionRepository;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowerServiceImpl implements FollowerService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getFollowersByUserId(long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new EntityNotFoundException(String.format("User with id = %d not found", userId));
        }
        return subscriptionRepository.findByFolloweeId(userId).stream()
                .map(userMapper::toDto)
                .toList();
    }
}
