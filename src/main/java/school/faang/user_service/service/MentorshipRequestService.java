package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.MentorshipRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.MentorshipRequestFilter;
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentorshipRequestService {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;
    private final List<MentorshipRequestFilter> mentorshipRequestFilters;

    private static final int MONTHS_BETWEEN_REQUESTS = 3;

    public MentorshipRequestDto requestMentorship(MentorshipRequestDto requestDto) {
        if (requestDto.getDescription() == null || requestDto.getDescription().isBlank()) {
            log.error("requestMentorship description is null or empty");
            throw new IllegalArgumentException("Description of request #" + requestDto.getId() + " is required");
        }

        User requester = userRepository.findById(requestDto.getRequesterId())
                .orElseThrow(() -> {
                    log.error("requestMentorship requester for request #{} not found", requestDto.getId());
                    return new IllegalArgumentException("Requester for request #" + requestDto.getId() + "  not found");
                });
        User receiver = userRepository.findById(requestDto.getReceiverId())
                .orElseThrow(() -> {
                    log.error("requestMentorship receiver for request #{} not found", requestDto.getId());
                    return new IllegalArgumentException("Receiver not found");
                });

        if (Objects.equals(requester.getId(), receiver.getId())) {
            log.error("requestMentorship for request #{} already requested", requestDto.getId());
            throw new IllegalArgumentException("Requester and receiver cannot be the same person for request #" + requestDto.getId());
        }

        Optional<MentorshipRequest> lastRequest = mentorshipRequestRepository.findLatestRequest(
                requestDto.getRequesterId(), requestDto.getReceiverId()
        );

        lastRequest.ifPresent(request -> {
            if (request.getCreatedAt().plusMonths(MONTHS_BETWEEN_REQUESTS).isAfter(LocalDateTime.now())) {
                log.error("requestMentorship for request #{} already requested in the last 3 months", request.getId());
                throw new IllegalArgumentException("You can only request mentorship once every 3 months, request #" + request.getId());
            }
        });

        log.info("requestMentorship #{} creating request", requestDto.getId());
        long requesterId = requestDto.getRequesterId();
        long receiverId = requestDto.getReceiverId();
        String description = requestDto.getDescription();
        MentorshipRequest request = mentorshipRequestRepository.create(requesterId, receiverId, description);

        return mentorshipRequestMapper.toDto(request);
    }

    public List<MentorshipRequestDto> getRequests(RequestFilterDto filter) {
        List<MentorshipRequest> requests = (List<MentorshipRequest>) mentorshipRequestRepository.findAll();
        Stream<MentorshipRequest> requestsStream = requests.stream();

        for (MentorshipRequestFilter requestFilter : mentorshipRequestFilters) {
            if (requestFilter.isApplicable(filter)) {
                requestsStream = requestFilter.apply(requestsStream, filter);
            }
        }

        return requestsStream
                .map(mentorshipRequestMapper::toDto)
                .toList();
    }

    public MentorshipRequestDto acceptRequest(Long requestId) {
        MentorshipRequest request = mentorshipRequestRepository
                .findById(requestId)
                .orElseThrow(() -> {
                    log.error("acceptRequest for request #{} not found", requestId);
                    return new IllegalArgumentException("Request #" + requestId + " not found");
                });

        if (request.getStatus() != RequestStatus.PENDING) {
            log.error("acceptRequest request #{} already accepted or rejected", requestId);
            throw new IllegalArgumentException("Request #" + requestId + " already accepted or rejected");
        }

        User requester = request.getRequester();
        User receiver = request.getReceiver();

        if (receiver.getMentors().contains(requester)) {
            log.error("acceptRequest requester is already a mentor, request #{}", requestId);
            throw new IllegalArgumentException("Requester is already a mentor, request #" + requestId);
        }

        log.info("acceptRequest accepting request #{}", requestId);
        receiver.getMentors().add(requester);
        request.setStatus(RequestStatus.ACCEPTED);
        mentorshipRequestRepository.save(request);

        return mentorshipRequestMapper.toDto(request);
    }

    public MentorshipRequestDto rejectRequest(Long requestId, RejectionDto rejection) {
        MentorshipRequest request = mentorshipRequestRepository
                .findById(requestId)
                .orElseThrow(() -> {
                    log.error("rejectRequest request #{}  not found", requestId);
                    return new IllegalArgumentException("Request #" + requestId + " not found");
                });

        if (request.getStatus() != RequestStatus.PENDING) {
            log.error("rejectRequest request #{} already accepted or rejected", requestId);
            throw new IllegalArgumentException("Request #" + requestId + "  already accepted or rejected");
        }

        log.info("rejectRequest rejecting request #{}", requestId);
        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejection.getReason());
        mentorshipRequestRepository.save(request);

        return mentorshipRequestMapper.toDto(request);
    }
}
