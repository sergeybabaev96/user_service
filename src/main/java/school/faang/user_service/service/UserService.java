package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserFilterMapper;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserFilterMapper userFilterMapper;

    public boolean doesUserExist(long userId) {
        return userRepository.existsById(userId);
    }

    public User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new DataRetrievalFailureException(
                        "User with id %d is not found".formatted(userId)));
    }

    public List<UserDto> getPremiumUsers(UserFilterDto userFilterDto) {
        Stream<User> userStream = userRepository.findPremiumUsers();
        if (userFilterDto == null) { //userFilterDto фильтр пользователей может быть null, если фильтрация не требуется.
            return userStream.map(userMapper::toDto).toList();
        }
        return userStream.filter(userFilterMapper.toEntity(userFilterDto))
                .map(userMapper::toDto)
                .toList();
    }
}
