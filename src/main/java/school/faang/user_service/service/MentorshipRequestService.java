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
import school.faang.user_service.mapper.MentorshipRequestMapper;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentorshipRequestService {
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final UserRepository userRepository;
    private final MentorshipRequestMapper mentorshipRequestMapper;

    private static final int MONTHS_BETWEEN_REQUESTS = 3;

    public MentorshipRequestDto requestMentorship(MentorshipRequestDto requestDto) {
        if (requestDto.getDescription() == null || requestDto.getDescription().isBlank()) {
            log.error("requestMentorship description is null or empty");
            throw new IllegalArgumentException("Description is required");
        }

        User requester = userRepository.findById(requestDto.getRequesterId())
                .orElseThrow(() -> {
                    log.error("requestMentorship requester not found");
                    return new IllegalArgumentException("Requester not found");
                });
        User receiver = userRepository.findById(requestDto.getReceiverId())
                .orElseThrow(() -> {
                    log.error("requestMentorship receiver not found");
                    return new IllegalArgumentException("Receiver not found");
                });

        if (Objects.equals(requester.getId(), receiver.getId())) {
            log.error("requestMentorship already requested");
            throw new IllegalArgumentException("Requester and receiver cannot be the same person");
        }

        Optional<MentorshipRequest> lastRequest = mentorshipRequestRepository.findLatestRequest(
                requestDto.getRequesterId(), requestDto.getReceiverId()
        );

        lastRequest.ifPresent(request -> {
            if (request.getCreatedAt().plusMonths(MONTHS_BETWEEN_REQUESTS).isAfter(LocalDateTime.now())) {
                log.error("requestMentorship already requested in the last 3 months");
                throw new IllegalArgumentException("You can only request mentorship once every 3 months");
            }
        });

        log.info("requestMentorship creating request");
        long requesterId = requestDto.getRequesterId();
        long receiverId = requestDto.getReceiverId();
        String description = requestDto.getDescription();
        MentorshipRequest request = mentorshipRequestRepository.create(requesterId, receiverId, description);

        return mentorshipRequestMapper.toDto(request);
    }

    public List<MentorshipRequestDto> getRequests(RequestFilterDto filter) {
        List<MentorshipRequest> requests = (List<MentorshipRequest>) mentorshipRequestRepository.findAll();

        List<MentorshipRequestDto> mentorshipRequestDtos = requests.stream()
                .map(mentorshipRequestMapper::toDto)
                .toList();

        return mentorshipRequestDtos.stream()
                .filter(request -> filter.getDescription() == null || request.getDescription().contains(filter.getDescription()))
                .filter(request -> filter.getRequesterId() == null || Objects.equals(request.getRequesterId(), filter.getRequesterId()))
                .filter(request -> filter.getReceiverId() == null || Objects.equals(request.getReceiverId(), filter.getReceiverId()))
                .filter(request -> filter.getStatus() == null || request.getStatus() == filter.getStatus())
                .collect(Collectors.toList());
    }

    public MentorshipRequestDto acceptRequest(Long requestId) {
        MentorshipRequest request = mentorshipRequestRepository
                .findById(requestId)
                .orElseThrow(() -> {
                    log.error("acceptRequest request not found");
                    return new IllegalArgumentException("Request not found");
                });

        if (request.getStatus() != RequestStatus.PENDING) {
            log.error("acceptRequest request already accepted or rejected");
            throw new IllegalArgumentException("Request already accepted or rejected");
        }

        User requester = request.getRequester();
        User receiver = request.getReceiver();

        if (receiver.getMentors().contains(requester)) {
            log.error("acceptRequest requester is already a mentor");
            throw new IllegalArgumentException("Requester is already a mentor");
        }

        log.info("acceptRequest accepting request");
        receiver.getMentors().add(requester);
        request.setStatus(RequestStatus.ACCEPTED);
        mentorshipRequestRepository.save(request);

        return mentorshipRequestMapper.toDto(request);
    }

    public MentorshipRequestDto rejectRequest(Long requestId, RejectionDto rejection) {
        MentorshipRequest request = mentorshipRequestRepository
                .findById(requestId)
                .orElseThrow(() -> {
                    log.error("rejectRequest request not found");
                    return new IllegalArgumentException("Request not found");
                });

        if (request.getStatus() != RequestStatus.PENDING) {
            log.error("rejectRequest request already accepted or rejected");
            throw new IllegalArgumentException("Request already accepted or rejected");
        }

        log.info("rejectRequest rejecting request");
        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejection.getReason());
        mentorshipRequestRepository.save(request);

        return mentorshipRequestMapper.toDto(request);
    }
}
