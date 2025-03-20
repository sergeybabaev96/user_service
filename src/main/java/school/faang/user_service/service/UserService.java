package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.validator.user.UserValidator;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final UserMapper userMapper;

    public UserDto getUserById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        return userOptional.map(userMapper::toUser).orElse(null);
    }

    public List<UserDto> getAllUsersFromListById(List<Long> userIds) {
        List<User> users = userRepository.findAllById(userIds);
        return userMapper.toListUserDto(users);
    }
}
