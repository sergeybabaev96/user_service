package school.faang.user_service.validator.recommendation;

import com.sun.jdi.request.DuplicateRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.SkillRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.recommendation.ErrorMessage;
import school.faang.user_service.exception.recommendation.RequestStatusException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationRequestValidator {
    private static final int MONTHS_AFTER_PREVIOUS_RECOMMENDATION = 6;
    private final SkillRepository skillRepository;
    private final RecommendationRequestRepository recommendationRequestRepository;

    public void validateRecommendation(RecommendationRequestDto recommendation) {
        checkIfAcceptableTimeForRequest(recommendation);
        checkIfOfferedSkillsExist(recommendation);
    }

    public RecommendationRequest validateRecommendationFromBd(Long id) {
        return recommendationRequestRepository.findById(id).orElseThrow(() -> {
            log.error("RecommendationRequest with id {} not found", id);
            return new NoSuchElementException(String.format("There isn't recommendationRequest with id = %d", id));
        });
    }

    public void checkRequestsStatus(Long id, RequestStatus status) {
        if (!status.equals(RequestStatus.PENDING)) {
            log.error("Request with ID {}  has the status {}. Operation is not allowed.", id, status);
            throw new RequestStatusException(ErrorMessage.REQUEST_STATUS, id, status);
        }
    }

    private void checkIfAcceptableTimeForRequest(RecommendationRequestDto recommendationRequestDto) {
        recommendationRequestRepository
                .findLatestPendingRequest(recommendationRequestDto.getRequesterId(),
                        recommendationRequestDto.getReceiverId())
                .ifPresent(existingRequest -> {
                    if (existingRequest.getCreatedAt().isAfter(LocalDateTime.now().
                            minusMonths(MONTHS_AFTER_PREVIOUS_RECOMMENDATION))) {
                        log.error("Request creation failed: Time limit exceeded for requester {} to " +
                                        "request recommendation for receiver {} ({} months).",
                                recommendationRequestDto.getRequesterId(),
                                recommendationRequestDto.getReceiverId(),
                                MONTHS_AFTER_PREVIOUS_RECOMMENDATION);
                        throw new DuplicateRequestException(
                                String.format("There is already a pending request created less than %d months ago " +
                                                "by requester %s for receiver %s.",
                                        MONTHS_AFTER_PREVIOUS_RECOMMENDATION,
                                        recommendationRequestDto.getRequesterId(),
                                        recommendationRequestDto.getReceiverId()));
                    }
                });
    }

    private void checkIfOfferedSkillsExist(RecommendationRequestDto recommendationRequestDto) {
        List<SkillRequestDto> skillRequestDtoList = recommendationRequestDto.getSkillRequests();
        if (skillRequestDtoList == null || skillRequestDtoList.isEmpty()) {
            log.warn("No skill offers found for recommendation creation.");
            throw new NoSuchElementException("No skill offers found for recommendation creation.");
        }

        List<String> skillTitlesList = skillRequestDtoList.stream()
                .map(SkillRequestDto::getSkillTitle)
                .toList();

        for (String skillTitle : skillTitlesList) {
            if (!skillRepository.existsByTitle(skillTitle)) {
                log.error("Skill with title '{}' does not exist in the system. Recommendation creation failed.",
                        skillTitle);
                throw new NoSuchElementException(String.format(ErrorMessage.SKILL_NOT_EXIST, skillTitle));
            }
        }
    }

}
