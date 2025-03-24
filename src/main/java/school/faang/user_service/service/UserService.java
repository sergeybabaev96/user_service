package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.UserViewDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.UserRepository;

@Slf4j
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
    public User getUser(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.error("Ошибка: Не удалось найти пользователя с ID {}", userId);
            return new DataValidationException("Пользователь не найден");
        });
    }

    /**
     *  Метод нужен для получения юзера из других сервисов
     * @param userId айди пользователя
     * @return Dto обьеккт
     */
    public UserViewDto getUserForOtherService(long userId) {
         return userMapper.toViewDto(getUser(userId));
    }
}
