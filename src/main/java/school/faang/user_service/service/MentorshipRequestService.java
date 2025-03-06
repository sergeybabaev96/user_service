package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.RejectionDto;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.MentorshipAlreadyExistsException;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class MentorshipRequestService {

    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final UserRepository userRepository;

    /*
    @Transactional
    public void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        validateMentorshipRequest(mentorshipRequestDto);

        MentorshipRequest mentorshipRequest = mentorshipRequestMapper.toEntity(mentorshipRequestDto);

        mentorshipRequestRepository.save(mentorshipRequest);
    }*/

    public void requestMentorship(MentorshipRequestDto mentorshipRequestDto) {

        validateMentorshipRequest(mentorshipRequestDto);

        MentorshipRequest mentorshipRequest = mentorshipRequestMapper.toEntity(mentorshipRequestDto);

                mentorshipRequestRepository.create(
                mentorshipRequestDto.getRequesterId(),
                mentorshipRequestDto.getReceiverId(),
                mentorshipRequestDto.getDescription());
    }

    private void validateMentorshipRequest(MentorshipRequestDto mentorshipRequestDto) {

        if (!(userRepository.existsById(mentorshipRequestDto.getRequesterId())
                && (userRepository.existsById(mentorshipRequestDto.getReceiverId())))) {
            log.info("Both of users need to be registered");
            throw new IllegalArgumentException("One of the users is not existed");
        }

        mentorshipRequestRepository.findLatestRequest(mentorshipRequestDto.getRequesterId(),
                        mentorshipRequestDto.getReceiverId())
                .ifPresent(mentorshipRequest -> {
                    LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
                    LocalDateTime createdAt = mentorshipRequest.getCreatedAt();
                    if (createdAt.isAfter(threeMonthsAgo) || createdAt.equals(threeMonthsAgo)) {
                        log.info("The request was made in the last 3 months, try later");
                        throw new IllegalArgumentException("The request was made in the last 3 months");
                    }
                });

        if (mentorshipRequestDto.getRequesterId() == mentorshipRequestDto.getReceiverId()) {
            log.info("The user can not be requester and receiver at the same time");
            throw new IllegalArgumentException("Requester user and receiver user is equal");
        }
    }

        /*
        Метод принимает объект класса RequestFilterDto - фильтры
        здесь могут быть следующими: по описанию, по автору запроса,
        по получателю запроса, по статусу запроса
        Используйте метод для поиска всех запросов из класса
        MentorshipRequestRepository, затем реализуйте систему
        фильтрации и добавьте возможность применять фильтр.
        */

    public List<MentorshipRequestDto> getRequests(RequestFilterDto filter) {

        Iterable<MentorshipRequest> mentorshipRequestIterable = mentorshipRequestRepository.findAll();

        List<MentorshipRequest> mentorshipRequests = StreamSupport.stream(mentorshipRequestIterable.spliterator(), false)
                .filter(request -> {
                    boolean result = true;
                    if (filter.getDescription() != null) {
                        result = result && request.getDescription().equals(filter.getDescription());
                    }
                    if (filter.getRequesterId() != null) {
                        result = result && request.getRequester().getId().equals(filter.getRequesterId());
                    }
                    if (filter.getReceiverId() != null) {
                        result = result && request.getReceiver().getId().equals(filter.getReceiverId());
                    }
                    if (filter.getStatus() != null) {
                        result = result && request.getStatus().equals(filter.getStatus());
                    }
                    return result;
                })
                .toList();

        // Преобразовать в DTO

        return new ArrayList<MentorshipRequestDto>();
    }

    /*
    В методе нужно найти нужный запрос в базе, если его нет, выкинуть исключение.
    Если ментор еще не является ментором отправителя,
    то добавить его в список менторов отправителя и
    сменить статус запроса на ACCEPTED.
    Если пользователь уже является ментором отправителя,
    выбросить исключение с сообщением об этом
    */
    @Transactional
    public void acceptRequest(long mentorshipRequestId) {

        MentorshipRequest mentorshipRequest = mentorshipRequestRepository.findById(mentorshipRequestId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Mentorship request with id %d not found", mentorshipRequestId)));

        User requester = mentorshipRequest.getRequester();
        User futureMentor = mentorshipRequest.getReceiver();

        if (!requester.getMentors().contains(futureMentor)) {
            requester.getMentors().add(futureMentor);
            futureMentor.getMentees().add(requester);
            mentorshipRequest.setStatus(RequestStatus.ACCEPTED);

            userRepository.save(requester);
            userRepository.save(futureMentor);
            mentorshipRequestRepository.save(mentorshipRequest);
        } else {
            throw new MentorshipAlreadyExistsException(
                    String.format("User %d is already a mentor for user %d",
                            futureMentor.getId(), requester.getId()));
        }
    }

    @Transactional
    public void rejectRequest(long mentorshipRequestId, RejectionDto rejection) {
        MentorshipRequest mentorshipRequest = mentorshipRequestRepository.findById(mentorshipRequestId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Mentorship request with id %d not found", mentorshipRequestId)));

        mentorshipRequestMapper.updateRequestFromDto(rejection, mentorshipRequest);
        mentorshipRequestRepository.save(mentorshipRequest);
    }
}
