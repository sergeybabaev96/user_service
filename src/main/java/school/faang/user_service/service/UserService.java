package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.validator.user.UserValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final UserMapper userMapper;

    public UserDto getUser(Long userId) {
        userValidator.checkUserExistsById(userId);
        User user = userRepository.findById(userId).get();
        return userMapper.toUser(user);
    }

    public List<UserDto> getUsersByIds(List<Long> userIds) {
        userIds.forEach(userValidator::checkUserExistsById);
        List<User> users = userRepository.findAllById(userIds);
        return userMapper.toListUserDto(users);
    }
}
