package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.user.UserViewDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import school.faang.user_service.service.user.UserService;

import java.util.List;

/**
 * Сервис для управления отношениями между пользователями в системе наставничества.
 * <p>
 * Этот сервис предоставляет методы для получения списка менти и менторов, а также для удаления связей между ними.
 * </p>
 *
 * <p>
 * Основные функции:
 * <ul>
 *      <li>{@link #getMentees(long) Получение списка менти} для заданного пользователя.</li>
 *      <li>{@link #getMentors(long) Получение списка менторов} для заданного пользователя.</li>
 *      <li>{@link #deleteMentee(long, long) Удаление связи между ментором и менти}.</li>
 *      <li>{@link #deleteMentor(long, long) Удаление связи между менти и ментором}.</li>
 * </ul>
 * </p>
 *
 * @author gulnaz21
 * @see User
 * @see MentorshipRepository
 * @see UserMapper
 * @see UserService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MentorshipService {
    private final MentorshipRepository mentorshipRepository;
    private final UserMapper userMapper;
    private final UserService userService;

    /**
     * Получение списка менти для заданного пользователя.
     *
     * @param userId id пользователя, для которого нужно получить список менти
     * @return Список объектов {@link UserViewDto}, представляющих менти указанного пользователя
     */
    public List<UserViewDto> getMentees(long userId) {
        User user = userService.getUserEntity(userId);
        List<User> mentees = user.getMentees();
        return mentees.stream()
                .map(this::mapUserToUserDto)
                .toList();
    }

    /**
     * Получение списка менторов для заданного пользователя.
     *
     * @param userId id пользователя, для которого нужно получить список менторов
     * @return Список объектов {@link UserViewDto}, представляющих менторов указанного пользователя
     */
    public List<UserViewDto> getMentors(long userId) {
        User user = userService.getUserEntity(userId);
        List<User> mentors = user.getMentors();
        return mentors.stream()
                .map(this::mapUserToUserDto)
                .toList();
    }

    /**
     * Удаляет менти из списка менти у ментора.
     *
     * @param menteeId Идентификатор менти, которого нужно удалить.
     * @param mentorId Идентификатор ментора, у которого нужно удалить менти.
     * @throws DataValidationException Если менти с указанным id не найден среди менти ментора.
     */
    public void deleteMentee(long menteeId, long mentorId) {
        User mentor = userService.getUserEntity(mentorId);
        List<User> mentees = mentor.getMentees();

        boolean isRemoved = removeUserFromList(mentees, menteeId);

        if (!isRemoved) {
            log.error("Ошибка: Не удалось найти менти {} у ментора {}", menteeId, mentorId);
            throw new DataValidationException("Менти не найден");
        }
        mentor.setMentees(mentees);
        mentorshipRepository.save(mentor);
    }

    /**
     * Удаляет ментора из списка менторов у менти.
     *
     * @param menteeId Идентификатор менти, у которого нужно удалить ментора.
     * @param mentorId Идентификатор ментора, которого нужно удалить.
     * @throws DataValidationException Если ментор с указанным id не найден среди менторов менти.
     */
    public void deleteMentor(long menteeId, long mentorId) {
        User mentee = userService.getUserEntity(menteeId);
        List<User> mentors = mentee.getMentors();

        boolean isRemoved = removeUserFromList(mentors, mentorId);
        if (!isRemoved) {
            log.error("Ошибка: Не удалось найти ментора {} у менти {}", mentorId, menteeId);
            throw new DataValidationException("Ментор не найден");
        }
        mentee.setMentors(mentors);
        mentorshipRepository.save(mentee);
    }

    /**
     * Преобразовывает объект {@link User} в объект {@link UserViewDto},
     * дополняя его id менти и менторов.
     *
     * @param user Объект пользователя, который нужно преобразовать.
     * @return Объект {@link UserViewDto}, содержащий информацию о пользователе,
     * дополненную id менти и менторов.
     */
    private UserViewDto mapUserToUserDto(User user) {
        List<Long> menteesIds = user.getMentees().stream()
                .map(User::getId)
                .toList();
        List<Long> mentorsIds = user.getMentors().stream()
                .map(User::getId)
                .toList();
        UserViewDto userDto = userMapper.toViewDto(user);
        userDto.setMenteesIds(menteesIds);
        userDto.setMentorsIds(mentorsIds);
        return userDto;
    }

    /**
     * Удаляет пользователя с указанным id из переданного списка пользователей.
     *
     * @param users  Список пользователей, из которого нужно удалить пользователя.
     * @param userId id пользователя, которого нужно удалить.
     * @return true, если пользователь был успешно удален, иначе false.
     */
    private boolean removeUserFromList(List<User> users, long userId) {
        return users.removeIf(user -> user.getId() == userId);
    }
}
