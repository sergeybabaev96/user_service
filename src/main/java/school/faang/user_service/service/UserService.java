package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public boolean doesUserExist(long userId) {
        return userRepository.existsById(userId);
    }

    public User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new DataRetrievalFailureException(
                        "User with id %d is not found".formatted(userId)));
    }

    public User findById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new DataValidationException("User not found"));
    }

    public boolean existsById(long userId) {
        return userRepository.existsById(userId);
    }

    public UserDto getUser(long userId) {
        var user = getUserById(userId);

        return userMapper.toDto(user);
    }

    public List<UserDto> getUsersByIds(List<Long> ids) {
        var users = userRepository.findAllById(ids);

        return userMapper.toDtoList(users);
    }
}
