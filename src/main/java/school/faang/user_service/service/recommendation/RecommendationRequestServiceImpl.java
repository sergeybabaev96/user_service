package school.faang.user_service.service.recommendation;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.RecommendationEvent;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RecommendationRequestRcvDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.publisher.recommendation.RecommendationEventPublisher;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.recommendation.filter.RecommendationRequestFilter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
@Slf4j
public class RecommendationRequestServiceImpl implements RecommendationRequestService {
    private static final int REQUEST_PERIOD_OF_THE_SAME_USER = 6;
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final RecommendationRequestMapper mapper;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final List<RecommendationRequestFilter> recommendationRequestFilters;
    private final RecommendationEventPublisher publisher;

    @Override
    public RecommendationRequestDto createRequest(RecommendationRequestRcvDto requestDto) {
        validateRecommendationRequest(requestDto);
        log.info("create request: {}", requestDto);
        RecommendationRequest request = convertRequestDtoToEntity(requestDto);
        RecommendationRequest requestSaved = recommendationRequestRepository.save(request);
        List<SkillRequest> skills = requestDto.skillIds().stream()
                .map(skillId -> skillRequestRepository.create(requestSaved.getId(), skillId))
                .toList();
        requestSaved.setSkills(skills);
        createRecommendationPublisherEvent(requestSaved.getRequester().getId(),
                requestSaved.getReceiver().getId(), requestSaved.getId());
        return mapper.toRecommendationRequestDto(requestSaved);
    }

    @Override
    public List<RecommendationRequestDto> getRequests(RequestFilterDto filters) {
        Stream<RecommendationRequest> requests = recommendationRequestRepository.findAll().stream();
        return recommendationRequestFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .reduce(requests,
                        (requestStream, filter) -> filter.apply(requestStream, filters),
                        (s1, s2) -> s1)
                .map(mapper::toRecommendationRequestDto)
                .toList();
    }

    @Override
    public RecommendationRequestDto getRequest(long id) {
        return mapper.toRecommendationRequestDto(recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(String
                        .format("Recommendation request with id %d not found", id))));
    }

    @Override
    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejectionDto) {
        log.info("rejectRequest with id: {} reason is {}", id, rejectionDto.reason());
        RecommendationRequest request = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(String
                        .format("Recommendation request id %d not found", id)));
        validateRequestForReject(request);
        request.setStatus(RequestStatus.REJECTED);
        request.setUpdatedAt(LocalDateTime.now());
        request.setRejectionReason(rejectionDto.reason());
        return mapper.toRecommendationRequestDto(recommendationRequestRepository.save(request));
    }

    private void validateRequestForReject(RecommendationRequest request) {
        long id = request.getId();
        if (RequestStatus.ACCEPTED.equals(request.getStatus())) {
            throw new IllegalArgumentException(
                    String.format("The recommendation request id %d is already accepted", id));
        }
        if (RequestStatus.REJECTED.equals(request.getStatus())) {
            throw new IllegalArgumentException(
                    String.format("The recommendation request id %d is already rejected", id));
        }
    }

    private RecommendationRequest convertRequestDtoToEntity(RecommendationRequestRcvDto requestDto) {
        RecommendationRequest request = mapper.toRecommendationRequestEntity(requestDto);
        User requester = getUserById(requestDto.requesterId());
        User receiver = getUserById(requestDto.receiverId());
        request.setRequester(requester);
        request.setReceiver(receiver);
        request.setStatus(RequestStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());
        return request;
    }

    private void validateRecommendationRequest(RecommendationRequestRcvDto request) {
        if (request.requesterId().equals(request.receiverId())) {
            throw new IllegalArgumentException(String
                    .format("The user with id %d cannot send a request to himself", request.requesterId()));
        }
        Optional<RecommendationRequest> lastRequest = recommendationRequestRepository.findLatestPendingRequest(
                request.requesterId(), request.receiverId());
        if (lastRequest.isPresent()) {
            LocalDateTime lastRequestDate = lastRequest.get().getCreatedAt();
            if (lastRequestDate.isAfter(LocalDateTime.now().minusMonths(REQUEST_PERIOD_OF_THE_SAME_USER))) {
                throw new IllegalArgumentException(String.format("Recommendation request must be sent once in %d months"
                                + ", the previous request with id = %d was no more than %d months ago",
                        REQUEST_PERIOD_OF_THE_SAME_USER, lastRequest.get().getId(), REQUEST_PERIOD_OF_THE_SAME_USER));
            }
        }
        for (long id : request.skillIds()) {
            if (!skillRepository.existsById(id)) {
                throw new IllegalArgumentException(String.format("Skill with id = %d not exist", id));
            }
        }
    }

    private User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(String.format("User with id %d not found", id)));
    }

    private void createRecommendationPublisherEvent(long requesterId, long receiverId, long recommendationId) {
        RecommendationEvent event = new RecommendationEvent(requesterId, receiverId, recommendationId);
        publisher.publish(event);
    }
}
