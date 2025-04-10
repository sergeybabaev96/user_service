package school.faang.user_service.service.user;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserViewDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

@Slf4j
@Data
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Получение пользователя по указанному идентификатору.
     *
     * @param userId Идентификатор пользователя.
     * @return Объект пользователя.
     * @throws DataValidationException Если пользователь с указанным идентификатором не найден.
     */
    public UserViewDto getUser(long userId) {
        log.info("Запрос на получение данных по пользователю с ID: {}", userId);
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.error("Ошибка: Не удалось найти пользователя с ID {}", userId);
            return new DataValidationException("Пользователь не найден");
        });

        log.info("Найден пользователь с ID: {}", userId);
        return userMapper.toViewDto(user);
    }


    /**
     * Получение пользователя по указанному идентификатору.
     *
     * @param userId Идентификатор пользователя.
     * @return Объект пользователя.
     * @throws DataValidationException Если пользователь с указанным идентификатором не найден.
     */
    public User getUserEntity(long userId) {
        log.info("Запрос на получение данных по пользователю с ID: {}", userId);
        return userRepository.findById(userId).orElseThrow(() -> {
            log.error("Ошибка: Не удалось найти пользователя с ID {}", userId);
            return new DataValidationException("Пользователь не найден");
        });

    }

    /**
     * Получение пользователей по указанным идентификаторам.
     *
     * @param ids Список идентификаторов пользователей.
     * @return Список объектов пользователей.
     */
    public List<UserViewDto> getUsersByIds(@NonNull List<Long> ids) {
        log.info("Запрос на получение данных по пользователям с ID's: {}", ids);
        List<User> users = userRepository.findAllById(ids);

        log.info("Найдены пользователя с ID's: {}", ids);
        return users.stream().map(userMapper::toViewDto).toList();
    }

}
