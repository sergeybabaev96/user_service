package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDro;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

@Service
@RequiredArgsConstructor
public class MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;

    public void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {

        // Проверить, что оба пользователя есть в базе данных
        // Запрос на менторство можно делать раз в три месяца
        // Нельзя отправлять запрос самому себе

        // Используйте метод для создания пользователя из класса MentorshipRequestRepository.
        // Используйте MapStruct для преобразований.

        if (userRepository.existsById(mentorshipRequestDto.getRequester())
                && (userRepository.existsById(mentorshipRequestDto.getReceiver()))) {
            System.out.println("Users exists!");
        }

        MentorshipRequest mentorshipRequest = mentorshipRequestRepository.create(
                mentorshipRequestDto.getRequester(),
                mentorshipRequestDto.getReceiver(),
                mentorshipRequestDto.getDescription());
    }

    public void getRequests(RequestFilterDro filter) {
        /*
        Используйте метод для поиска всех запросов из класса
        MentorshipRequestRepository, затем реализуйте систему
        фильтрации и добавьте возможность применять фильтр.
        */
    }

    public void acceptRequest(long id) {
        /*
        В методе нужно найти нужный запрос в базе, если его нет, выкинуть исключение. Если ментор еще не
        является ментором отправителя, то добавить его в список менторов отправителя и
        сменить статус запроса на ACCEPTED. Если пользователь уже является ментором отправителя,
        выбросить исключение с сообщением об этом
         */
    }

    public void rejectRequest(long id, RejectionDto rejection) {

    }
}
