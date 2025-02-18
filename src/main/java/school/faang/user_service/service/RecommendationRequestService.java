package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import school.faang.user_service.dto.request.RejectionDto;
import school.faang.user_service.dto.request.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.Filter;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Validated
@Service
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final SkillRequestService skillRequestService;
    private final List<Filter<RequestFilterDto, RecommendationRequest>> filters;

    @Transactional
    public RecommendationRequest create(
            Long requesterId, Long receiverId, @NotBlank String message, List<Long> skillIds) {
        validateUserExistence(requesterId);
        validateUserExistence(receiverId);
        validateSkillsExist(skillIds);

        final RecommendationRequest[] result = new RecommendationRequest[1];
        Optional<RecommendationRequest> latestRecommendationRequest =
                recommendationRequestRepository.findLatestPendingRequest(requesterId, receiverId);

        latestRecommendationRequest.ifPresentOrElse(rec ->
                        updateExistingRecommendation(skillIds, rec, result),
                () -> creatNewRecommendation(requesterId, receiverId, message, skillIds, result)
        );

        return result[0];
    }

    public List<RecommendationRequest> getAllRequests(RequestFilterDto filterDto) {
        Stream<RecommendationRequest> allRecommendation =
                recommendationRequestRepository.findAll().stream();
        return filters.stream()
                .filter(f -> f.isApplicable(filterDto))
                .reduce(allRecommendation, (stream, filter) ->
                        filter.apply(stream, filterDto), (s1, s2) -> s1)
                .toList();
    }

    public RecommendationRequest getRequestById(Long id) {
        return recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Not Found: " + id));
    }

    public RecommendationRequest rejectRequest(Long recommendationRequestId, RejectionDto rejectionDto) {

        RecommendationRequest entity = getRequestById(recommendationRequestId);
        if (entity.getStatus() == RequestStatus.PENDING) {
            entity.setStatus(RequestStatus.REJECTED);
            entity.setRejectionReason(rejectionDto.reason());
            entity.setUpdatedAt(LocalDateTime.now());
            return recommendationRequestRepository.save(entity);
        }

        return entity;
    }

    private RecommendationRequest creatNewRecommendation(
            Long requesterId, Long receiverId, String message, List<Long> skillIds, RecommendationRequest[] result) {
        RecommendationRequest rec = new RecommendationRequest();
        rec.setMessage(message);
        rec.setRequester(userRepository.findById(requesterId).orElseThrow());
        rec.setReceiver(userRepository.findById(receiverId).orElseThrow());
        rec.setSkillsRequests(skillRequestService.createSkillRequests(rec, skillIds));
        rec.setStatus(RequestStatus.PENDING);
        result[0] = recommendationRequestRepository.save(rec);
        return result[0];
    }

    private RecommendationRequest updateExistingRecommendation(
            List<Long> skillIds, RecommendationRequest rec, RecommendationRequest[] result) {
        LocalDateTime nowAfterAccept = LocalDateTime.now().minusMonths(6);

        if (rec.getCreatedAt().isBefore(nowAfterAccept)) {
            rec.setStatus(RequestStatus.PENDING);
            rec.setCreatedAt(LocalDateTime.now());
            rec.setUpdatedAt(LocalDateTime.now());
            rec.setSkillsRequests(skillRequestService.createSkillRequests(rec, skillIds));
            result[0] = recommendationRequestRepository.save(rec);
            return result[0];
        } else {
            throw new IllegalArgumentException(
                    "The recommendation already exists and less than 6 months have passed since the last request.");
        }
    }

    private void validateUserExistence(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(String.format("User not found with id: %d", userId));
        }
    }

    private void validateSkillsExist(List<Long> skillsIds) {
        if (skillsIds == null || skillsIds.isEmpty()) {
            throw new IllegalArgumentException("Some provided skill IDs do not exist in request");
        }

        int countSkill = skillRepository.countExisting(skillsIds);
        if (countSkill != skillsIds.size()) {
            throw new IllegalArgumentException("Some provided skill IDs do not exist in the database");
        }
    }
}
