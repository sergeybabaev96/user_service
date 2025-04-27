package school.faang.user_service.service.user;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserViewDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.List;

/**
 * Сервис для работы с пользователями.
 * <p>
 * Предоставляет методы для получения информации о пользователях,
 * включая базовую информацию и детализированные данные.
 * </p>
 */
@Slf4j
@Data
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Получает DTO пользователя по идентификатору.
     *
     * @param userId идентификатор пользователя (должен быть > 0)
     * @return DTO с данными пользователя
     * @throws DataValidationException если пользователь не найден
     * @see UserViewDto
     */
    public UserViewDto getUser(Long userId) {
        log.info("Getting user by ID: {}", userId);
        User user = getUserEntity(userId);
        return userMapper.toViewDto(user);
    }

    /**
     * Получает сущность пользователя по идентификатору.
     *
     * @param userId идентификатор пользователя (должен быть > 0)
     * @return сущность пользователя
     * @throws DataValidationException если пользователь не найден
     * @see User
     */
    public User getUserEntity(Long userId) {
        log.info("Запрос на получение данных по пользователю с ID: {}", userId);
        return userRepository.findById(userId).orElseThrow(() -> {
            log.error("Ошибка: Не удалось найти пользователя с ID {}", userId);
            return new DataValidationException("Пользователь не найден");
        });

    }

    /**
     * Получает список DTO пользователей по их идентификаторам.
     *
     * @param ids список идентификаторов пользователей (не может быть null)
     * @return список DTO пользователей (может быть пустым)
     * @see UserViewDto
     */
    public List<UserViewDto> getUsersByIds(List<Long> ids) {
        log.info("Запрос на получение данных по пользователям с ID's: {}", ids);
        List<User> users = userRepository.findAllById(ids);

        log.info("Найдены пользователя с ID's: {}", ids);
        return users.stream().map(userMapper::toViewDto).toList();
    }

    /**
     * Получает базовую информацию о пользователе.
     *
     * @param userId идентификатор пользователя (должен быть > 0)
     * @return DTO с базовой информацией о пользователе
     * @throws DataValidationException если пользователь не найден
     * @see UserDto
     */
    public UserDto getUserForService(Long userId) {
        log.info("Getting basic user info for ID: {}", userId);
        User user = getUserEntity(userId);
        return userMapper.toUserDto(user);
    }
}