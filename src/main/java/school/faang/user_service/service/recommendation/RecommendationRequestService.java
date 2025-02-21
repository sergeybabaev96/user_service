package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.*;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.RecommendationRequestCreatedException;
import school.faang.user_service.exception.RequestAlreadyProcessedException;
import school.faang.user_service.exception.ResourceNotFoundException;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.messaging.kafka.RecommendationRequestedEventPublisher;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.service.SkillRequestService;
import school.faang.user_service.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecommendationRequestService {

    private final RecommendationRequestRepository recommendationRequestRepository;
    private final UserService userService;
    private final SkillRequestService skillRequestService;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final RecommendationRequestFilter recommendationRequestFilter;
    private final RecommendationRequestedEventPublisher eventPublisher;

    @Transactional
    public CreateRecommendationRequestResponse create(CreateRecommendationRequestRequest createRecommendationRequestRequest) {
        long requesterId = createRecommendationRequestRequest.requesterId();
        long receiverId = createRecommendationRequestRequest.receiverId();

        User requester = userService.findById(requesterId);
        User receiver = userService.findById(receiverId);

        isSixMonthLeft(requesterId, receiverId);

        RecommendationRequest mappedRecommendationRequest
                = recommendationRequestMapper.toEntity(createRecommendationRequestRequest, requester, receiver);

        RecommendationRequest recommendationRequest = recommendationRequestRepository.save(mappedRecommendationRequest);
        List<SkillRequest> skillRequests
                = skillRequestService.createSkillRequests(recommendationRequest.getId(), createRecommendationRequestRequest.skills());
        recommendationRequest.setSkills(skillRequests);

        RecommendationRequestedEvent event = new RecommendationRequestedEvent(
                recommendationRequest.getRequester().getId(),
                recommendationRequest.getReceiver().getId(),
                recommendationRequest.getId()
        );
        eventPublisher.publishEvent(event);

        return recommendationRequestMapper.toCreateDto(recommendationRequest);
    }

    public List<GetRecommendationRequestResponse> getRequests(RequestFilterDto filter) {
        List<RecommendationRequest> recommendationRequests = recommendationRequestRepository.findAll();
        Predicate<RecommendationRequest> predicate = recommendationRequestFilter.getPredicates(filter)
                .stream()
                .reduce(Predicate::and)
                .orElse(request -> true);
        return recommendationRequests.stream()
                .filter(predicate)
                .map(recommendationRequestMapper::toGetDto)
                .toList();
    }

    public GetRecommendationRequestResponse getRequest(long id) {
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.recommendationNotFoundException(id));
        return recommendationRequestMapper.toGetDto(recommendationRequest);
    }

    @Transactional
    public void rejectRequest(long id, RejectionDto rejection) {
        RecommendationRequest recommendationRequest = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.recommendationNotFoundException(id));
        if (recommendationRequest.getStatus().equals(RequestStatus.REJECTED)
                || recommendationRequest.getStatus().equals(RequestStatus.ACCEPTED)) {
            throw new RequestAlreadyProcessedException("The request has already been processed");
        }
        recommendationRequest.setStatus(RequestStatus.REJECTED);
        recommendationRequest.setRejectionReason(rejection.reason());
    }

    private void isSixMonthLeft(long requesterId, long receiverId) {
        recommendationRequestRepository.findLatestPendingRequest(requesterId, receiverId)
                .ifPresent(lastRequest -> {
                    boolean isLeft = lastRequest.getCreatedAt()
                            .plusMonths(6)
                            .isBefore(LocalDateTime.now());
                    if (!isLeft) {
                        throw new RecommendationRequestCreatedException("request can be submitted once every 6 month");
                    }
                });
    }
}
