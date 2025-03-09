package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestDto;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestDto;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    @Value("${recommendationRequestMinDistanceMonths}")
    private int recommendationRequestMinDistanceMonths;

    private final UserService userService;
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillRepository skillRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final SkillRequestMapper skillRequestMapper;

    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequest) {
        validateRecommendationRequest(recommendationRequest);

        recommendationRequest.skills().forEach(this::createSkillRequest);

        var recommendationRequestId = recommendationRequestRepository.create(
                recommendationRequest.requesterId(),
                recommendationRequest.receiverId(),
                recommendationRequest.message(),
                recommendationRequest.status(),
                recommendationRequest.createdAt(),
                recommendationRequest.updatedAt());

        return new RecommendationRequestDto(
                recommendationRequestId,
                recommendationRequest.message(),
                recommendationRequest.status(),
                recommendationRequest.skills(),
                recommendationRequest.requesterId(),
                recommendationRequest.receiverId(),
                recommendationRequest.createdAt(),
                recommendationRequest.updatedAt());
    }

    private void validateRecommendationRequest(RecommendationRequestDto recommendationRequest) {
        var requesterId = recommendationRequest.requesterId();
        if (!userService.existsById(requesterId)) {
            throw new DataValidationException(String.format(
                    "Requester with id %d is not found",
                    requesterId));
        }

        var receiverId = recommendationRequest.receiverId();
        if (!userService.existsById(receiverId)) {
            throw new DataValidationException(String.format(
                    "Receiver with id %d is not found",
                    receiverId));
        }

        var latestRequest = recommendationRequestRepository.findLatestRequest(requesterId, receiverId);
        if (latestRequest.isPresent()
                && LocalDateTime.now()
                .minusMonths(recommendationRequestMinDistanceMonths)
                .isBefore(latestRequest.get().getCreatedAt())) {
            throw new DataValidationException(String.format(
                    "Less than %d months have passed since the last recommendation request from user %d to user %d",
                    recommendationRequestMinDistanceMonths,
                    requesterId,
                    receiverId));
        }

        validateSkills(recommendationRequest);
    }

    private void validateSkills(RecommendationRequestDto recommendationRequest) {
        var existedSkillsInRecommendation = recommendationRequest.skills()
                .stream()
                .filter(dto -> skillRepository.existsById(dto.skillId()))
                .toList();
        if (existedSkillsInRecommendation.size() != recommendationRequest.skills().stream().distinct().count()) {
            throw new DataValidationException(String.format(
                    "Skills %s are not registered",
                    String.join(
                            ", ",
                            recommendationRequest.skills()
                                    .stream()
                                    .map(SkillRequestDto::skillId)
                                    .filter(skillId -> existedSkillsInRecommendation.stream()
                                            .filter(existedSkill -> existedSkill.skillId() == skillId)
                                            .findFirst()
                                            .isEmpty())
                                    .map(skillRepository::findById)
                                    .filter(Optional::isPresent)
                                    .map(x -> x.get().getTitle())
                                    .toList())));
        }
    }

    private SkillRequestDto createSkillRequest(SkillRequestDto skillRequestDto) {
        var skillRequest = skillRequestRepository.create(skillRequestDto.requestId(), skillRequestDto.skillId());

        return skillRequestMapper.toDto(skillRequest);
    }
}
