package school.faang.user_service.service.mentorship;

import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipResponseDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;

import java.util.List;

public interface MentorshipRequestService {

    /**
     * Mетод для создания нового запроса на менторство.
     * Метод проверяет, что
     * - пользователь, который запрашивает менторство и пользователь, у которого запрашивают - существуют в базе данных.
     * - учитывает, что запрос на менторство можно делать лишь раз в 3 месяца.
     * - пользователь не может отправить запрос сам себе.
     * В случае успешной проверки запрос на менторство сохраняется в базе данных.
     */
    MentorshipResponseDto requestMentorship(MentorshipRequestDto mentorshipRequestDto);

    /**
     * Метод возвращает все запросы на менторство, и применяет к ним фильтрацию.
     * Метод принимает объект класса RequestFilterDto, представляющий собой набор следующих фильтров:
     * по описанию, по автору запроса, по получателю запроса, по статусу запроса.
     */
    List<MentorshipResponseDto> getRequests(MentorshipRequestFilterDto filters);

    /**
     * Метод реализует возможность принять запрос на менторство, пришедший от другого пользователя.
     * Метод по полученному requestId находит нужный запрос в базе, а если его нет, выбрасывает исключение.
     * Если ментор еще не является ментором отправителя, то метод добавляет его в список менторов отправителя
     * и сменяет статус запроса на ACCEPTED.
     * Если пользователь уже является ментором отправителя - выбрасывает исключение с сообщением об этом.
     */
    void acceptRequest(long requestId);

    /**
     * Метод ищет нужный запрос в базе, если его нет, выбрасывает исключение.
     * Если запрос есть - меняет статус запроса на REJECTED и указывает причину отклонения
     * из поля reason из RejectionDto.
     */
    void rejectRequest(long id, RejectionDto rejection);
}
