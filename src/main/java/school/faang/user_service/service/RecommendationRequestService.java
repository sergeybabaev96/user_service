package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final RecommendationMapper recommendationMapper;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final SkillRequestRepository skillRequestRepository;

    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequest) {

        Long requesterId = recommendationRequest.getRequesterId();
        Long receiverId = recommendationRequest.getReceiverId();

        if (!userRepository.existsById(requesterId)) {
            throw new EntityNotFoundException("Requester not found with id: " + requesterId);
        }

        if (!userRepository.existsById(receiverId)) {
            throw new EntityNotFoundException("Receiver not found with id: " + receiverId);
        }

        Optional<RecommendationRequest> latestRequest = recommendationRequestRepository
                .findLatestPendingRequest(requesterId, receiverId);

        if (latestRequest.isPresent() && latestRequest.get().getCreatedAt()
                .isAfter(latestRequest.get().getUpdatedAt().minusMonths(6))) {
            throw new DataValidationException("You can send recommendation request to this user only once per 6 months");
        }

        latestRequest.get().getSkills().stream()
                .forEach(skillRequest -> {
                    if (!skillRepository.existsByTitle(skillRequest.getSkill().getTitle())) {
                        throw new DataValidationException("This skill is not in the database!");
                    }
                    skillRequestRepository.create(skillRequest.getId(), skillRequest.getSkill().getId());
                });

        RecommendationRequest newRequest = new RecommendationRequest();
        RecommendationRequest savedRequest = recommendationRequestRepository.save(newRequest);

        return recommendationMapper.toDto(savedRequest);
    }

    public List<RecommendationRequestDto> getRequests(RequestFilterDto filter) {
        List<RecommendationRequest> allRequests = recommendationRequestRepository.findAll();

        return allRequests.stream()
                .filter(request -> filterByRequesterId(request, filter.getRequesterId()))
                .filter(request -> filterByReceiverId(request, filter.getReceiverId()))
                .filter(request -> filterByRecommendationId(request, filter.getRecommendationId()))
                .filter(request -> filterByMessagePattern(request, filter.getMessagePattern()))
                .filter(request -> filterByCreatedAfter(request, filter.getCreatedAfter()))
                .filter(request -> filterByCreatedBefore(request, filter.getCreatedBefore()))
                .map(recommendationMapper::toDto)
                .collect(Collectors.toList());
    }

    private boolean filterByRequesterId(RecommendationRequest request, Long requesterId) {
        return requesterId == null || request.getRequester().getId().equals(requesterId);
    }

    private boolean filterByReceiverId(RecommendationRequest request, Long receiverId) {
        return receiverId == null || request.getReceiver().getId().equals(receiverId);
    }

    private boolean filterByRecommendationId(RecommendationRequest request, Long recommendationId) {
        return recommendationId == null ||
                (request.getRecommendation() != null &&
                        request.getRecommendation().getId() == recommendationId);
    }

    private boolean filterByMessagePattern(RecommendationRequest request, String messagePattern) {
        return messagePattern == null ||
                (request.getMessage() != null &&
                        request.getMessage().toLowerCase().contains(messagePattern.toLowerCase()));
    }

    private boolean filterByCreatedAfter(RecommendationRequest request, LocalDateTime createdAfter) {
        return createdAfter == null || request.getCreatedAt().isAfter(createdAfter);
    }

    private boolean filterByCreatedBefore(RecommendationRequest request, LocalDateTime createdBefore) {
        return createdBefore == null || request.getCreatedAt().isBefore(createdBefore);
    }

    public RecommendationRequestDto getRequest(long id) {
        RecommendationRequest request = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recommendation request not found with id: " + id));
        return recommendationMapper.toDto(request);
    }

    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejection) {
        RecommendationRequest request = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recommendation request not found with id: " + id));

        request.setRejectionReason(rejection.getReason());
        RecommendationRequest updatedRequest = recommendationRequestRepository.save(request);

        return recommendationMapper.toDto(updatedRequest);
    }
}