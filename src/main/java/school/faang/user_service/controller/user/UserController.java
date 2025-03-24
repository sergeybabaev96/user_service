package school.faang.user_service.controller.user;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.UserViewDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

/**
 * UserController реализует возможность получить одного пользователя по его идентификатору
 * или сразу список всех пользователей по списку id этих пользователей.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @GetMapping("/users/{userId}")
    UserViewDto getUser(@PathVariable long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.error("Ошибка: Не удалось найти пользователя с ID {}", userId);
            return new DataValidationException("Пользователь не найден");
        });
        return userMapper.toViewDto(user);
    }

    @PostMapping("/users")
    List<UserViewDto> getUsersByIds(@RequestBody @NonNull List<Long> ids) {
        List<User> users = userRepository.findAllById(ids);
        return users.stream().map(userMapper::toViewDto).toList();
    }
}
