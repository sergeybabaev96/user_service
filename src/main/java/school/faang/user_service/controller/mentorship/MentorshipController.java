package school.faang.user_service.controller.mentorship;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.user.UserViewDto;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.List;

/**
 * Контроллер для управления запросами, связанными с системой наставничества.
 * <p>
 * Этот класс отвечает за обработку запросов, связанных с системой наставничества,
 * для удаления связей между ними, а также передачу в сервисный слой для выполнения операций с данными.
 * </p>
 * <p>
 * Основные функции:
 * <ul>
 *     <li>{@link #getMentees(long) Получение списка менти} для заданного ментора.</li>
 *     <li>{@link #getMentors(long) Получение списка менторов} для заданного пользователя.</li>
 *     <li>{@link #deleteMentee(long, long) Удаление связи между ментором и менти}.</li>
 *     <li>{@link #deleteMentor(long, long) Удаление связи между менти и ментором}.</li>
 * </ul>
 * </p>
 *
 * @author gulnaz21
 * @see MentorshipService
 * @see UserViewDto
 */
@Controller
@RequiredArgsConstructor
public class MentorshipController {
    private final MentorshipService mentorshipService;

    /**
     * Получение списка менти для заданного пользователя.
     *
     * @param userId Идентификатор пользователя.
     * @return Список объектов {@link UserViewDto}, представляющих менти указанного пользователя.
     */
    public List<UserViewDto> getMentees(long userId) {
        return mentorshipService.getMentees(userId);
    }

    /**
     * Получение списка менторов для заданного пользователя.
     *
     * @param userId Идентификатор пользователя.
     * @return Список объектов {@link UserViewDto}, представляющих менторов указанного пользователя.
     */
    public List<UserViewDto> getMentors(long userId) {
        return mentorshipService.getMentors(userId);
    }

    /**
     * Удаление связи между ментором и менти.
     *
     * @param menteeId Идентификатор менти.
     * @param mentorId Идентификатор ментора.
     */
    public void deleteMentee(long menteeId, long mentorId) {
        mentorshipService.deleteMentee(menteeId, mentorId);
    }

    /**
     * Удаление связи между менти и ментором.
     *
     * @param menteeId Идентификатор менти.
     * @param mentorId Идентификатор ментора.
     */
    public void deleteMentor(long menteeId, long mentorId) {
        mentorshipService.deleteMentor(menteeId, mentorId);
    }
}
