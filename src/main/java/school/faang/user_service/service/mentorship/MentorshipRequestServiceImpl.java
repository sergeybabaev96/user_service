package school.faang.user_service.service.mentorship;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestDto;
import school.faang.user_service.dto.mentorship.MentorshipRequestFilterDto;
import school.faang.user_service.dto.mentorship.MentorshipResponseDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.events.MentorshipOfferedEvent;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.publisher.MentorshipOfferedEventPublisher;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.service.mentorship.filter.RequestFilter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentorshipRequestServiceImpl implements MentorshipRequestService {
    private static final int MIN_COUNT_OF_MONTHS_BETWEEN_REQUESTS = 3;
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;
    private final MentorshipRequestMapper mapper;
    private final List<RequestFilter> requestFilters;
    private final MentorshipOfferedEventPublisher mentorshipOfferedEventPublisher;

    @Override
    public MentorshipResponseDto requestMentorship(MentorshipRequestDto mentorshipRequestDto) {
        log.info("MentorshipRequestServiceImpl: method #requestMentorship started with data: {}", mentorshipRequestDto);

        validateRequest(mentorshipRequestDto);
        MentorshipRequest mentorshipRequest = mentorshipRequestRepository.create(
                mentorshipRequestDto.description(),
                mentorshipRequestDto.requester().getUserId(),
                mentorshipRequestDto.receiver().getUserId()
        );

        MentorshipOfferedEvent mentorshipOfferedEvent =
                mapper.toMentorshipOfferedEvent(mentorshipRequestDto, mentorshipRequest);

        mentorshipOfferedEventPublisher.publish(mentorshipOfferedEvent);

        return mapper.toMentorshipResponseDto(mentorshipRequest);
    }

    @Override
    public List<MentorshipResponseDto> getRequests(MentorshipRequestFilterDto filters) {
        log.info("MentorshipRequestServiceImpl: method #getRequests started with filters: {}", filters);
        Stream<MentorshipRequest> mentorshipRequests = mentorshipRequestRepository.findAll().stream();

        for (RequestFilter requestFilter : requestFilters) {
            if (requestFilter.isApplicable(filters)) {
                mentorshipRequests = requestFilter.apply(mentorshipRequests, filters);
            }
        }

        return mentorshipRequests
                .map(mapper::toMentorshipResponseDto)
                .toList();
    }

    @Override
    public void acceptRequest(long requestId) {
        log.info("MentorshipRequestServiceImpl: method #acceptRequest started with requestId: {}", requestId);
        MentorshipRequest request = mentorshipRequestRepository.findById(requestId).orElseThrow(
                () -> new IllegalArgumentException(String.format("Запрос с id: %d отсутствует в базе данных",
                        requestId)));

        User receiver = request.getReceiver();
        User requester = request.getRequester();

        requester.getMentors().stream()
                .filter(it -> receiver.getId().equals(it.getId()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format(
                        "Получатель запроса с id: %d уже является ментором отправителя", requestId)));

        requester.getMentors().add(receiver);
        request.setStatus(RequestStatus.ACCEPTED);
    }

    @Override
    public void rejectRequest(long requestId, RejectionDto rejection) {
        log.info("MentorshipRequestServiceImpl: method #rejectRequest started with requestId: {} and data: {}",
                requestId, rejection);
        MentorshipRequest mentorshipRequest = mentorshipRequestRepository.findById(requestId).orElseThrow(
                () -> new IllegalArgumentException(String.format("В базе данных отсутствует запрос с id: %d",
                        requestId)));
        mentorshipRequest.setStatus(RequestStatus.REJECTED);
        mentorshipRequest.setRejectionReason(rejection.reason());
    }

    private void validateRequest(MentorshipRequestDto mentorshipRequestDto) {
        checkRequesterAndReceiverAreNotTheSamePerson(mentorshipRequestDto);
        User requester = userRepository.findById(mentorshipRequestDto.requester().getUserId())
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("There is no requester with id = %s in database",
                                mentorshipRequestDto.requester().getUserId()))
                );
        User receiver = userRepository.findById(mentorshipRequestDto.receiver().getUserId())
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("There is no receiver with id = %s in database",
                                mentorshipRequestDto.requester().getUserId())));
        MentorshipRequest lastMentorshipRequest = mentorshipRequestRepository.findLatestRequest(
                requester.getId(), receiver.getId()).orElse(null);
        if (!Objects.isNull(lastMentorshipRequest)) {
            checkRequestIsNotTooOften(lastMentorshipRequest);
        }
    }

    private void checkRequesterAndReceiverAreNotTheSamePerson(MentorshipRequestDto mentorshipRequestDto) {
        if (mentorshipRequestDto.requester().getUserId().equals(mentorshipRequestDto.receiver().getUserId())) {
            throw new IllegalArgumentException("Пользователь не может отправлять запрос сам себе!");
        }
    }


    private void checkRequestIsNotTooOften(MentorshipRequest lastMentorshipRequest) {
        if (lastMentorshipRequest.getCreatedAt().isAfter(
                LocalDateTime.now().minusMonths(MIN_COUNT_OF_MONTHS_BETWEEN_REQUESTS))) {
            throw new IllegalArgumentException(
                    String.format("Запрос на менторство не может быть чаще чем раз в 3 месяца. "
                            + "Последний запрос был %s", lastMentorshipRequest.getCreatedAt()));
        }
    }
}
