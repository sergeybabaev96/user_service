package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.RecommendationRequestFilter;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.mapper.SkillRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.dto.recommendation.SkillRequestDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    @Value("${recommendationRequestMinDistanceMonths}")
    private int recommendationRequestMinDistanceMonths;

    private final UserService userService;
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillRepository skillRepository;
    private final SkillRequestService skillRequestService;
    private final RecommendationService recommendationService;
    private final SkillRequestMapper skillRequestMapper;
    private final RecommendationRequestMapper recommendationRequestMapper;

    public RecommendationRequestDto create(@NotNull RecommendationRequestDto recommendationRequest) {
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

    public List<RecommendationRequestDto> getRequests(@NotNull RequestFilterDto filterDto) {
        return recommendationRequestRepository.findAll()
                .stream()
                .map(this::fillRelativeObjectsInRecommendationRequestEntity)
                .filter(new RecommendationRequestFilter(filterDto))
                .map(recommendationRequestMapper::toDto)
                .toList();
    }

    public RecommendationRequestDto getRequest(long id) {
        var entity = recommendationRequestRepository.findById(id).orElseThrow(
                () -> new DataRetrievalFailureException("Recommendation with id %d is not found".formatted(id)));

        return recommendationRequestMapper.toDto(entity);
    }

    public boolean rejectRequest(long id, RejectionDto rejection) {
        var entity = recommendationRequestRepository.findById(id).orElseThrow(
                () -> new DataRetrievalFailureException("Recommendation with id %d is not found".formatted(id)));
        if (entity.getStatus() == RequestStatus.ACCEPTED || entity.getStatus() == RequestStatus.REJECTED) {
            return false;
        }

        entity.setStatus(RequestStatus.REJECTED);
        entity.setRejectionReason(rejection.reason());
        entity.setUpdatedAt(LocalDateTime.now());

        recommendationRequestRepository.save(entity);

        return true;
    }

    @NotNull
    private RecommendationRequest fillRelativeObjectsInRecommendationRequestEntity(RecommendationRequest entity) {
        var requester = userService.findById(entity.getRequester().getId());
        entity.setRequester(requester);

        var receiver = userService.findById(entity.getReceiver().getId());
        entity.setReceiver(receiver);

        var recommendation = recommendationService.findById(entity.getRecommendation().getId());
        entity.setRecommendation(recommendation);

        var skillRequests = entity.getSkills()
                .stream()
                .map(skillRequest -> skillRequestService.findById(skillRequest.getId()))
                .toList();
        entity.setSkills(skillRequests);

        return entity;
    }

    private void validateRecommendationRequest(RecommendationRequestDto recommendationRequest) {
        var requesterId = recommendationRequest.requesterId();
        if (!userService.existsById(requesterId)) {
            throw new DataValidationException("Requester with id %d is not found".formatted(requesterId));
        }

        var receiverId = recommendationRequest.receiverId();
        if (!userService.existsById(receiverId)) {
            throw new DataValidationException("Receiver with id %d is not found".formatted(receiverId));
        }

        var latestRequest = recommendationRequestRepository.findLatestRequest(requesterId, receiverId);
        if (latestRequest.isPresent()
                && LocalDateTime.now()
                .minusMonths(recommendationRequestMinDistanceMonths)
                .isBefore(latestRequest.get().getCreatedAt())) {
            throw new DataValidationException(
                    "Less than %d months have passed since the last recommendation request from user %d to user %d"
                            .formatted(
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
            throw new DataValidationException(
                    "Skills %s are not registered".formatted(
                            String.join(
                                    ", ",
                                    recommendationRequest.skills()
                                            .stream()
                                            .map(SkillRequestDto::skillId)
                                            .filter(skillId -> existedSkillsInRecommendation.stream()
                                                    .filter(
                                                            existedSkill -> existedSkill.skillId() == skillId)
                                                    .findFirst()
                                                    .isEmpty())
                                            .map(skillRepository::findById)
                                            .filter(Optional::isPresent)
                                            .map(x -> x.get().getTitle())
                                            .toList())));
        }
    }

    private SkillRequestDto createSkillRequest(SkillRequestDto skillRequestDto) {
        var skillRequest = skillRequestService.create(skillRequestDto.requestId(), skillRequestDto.skillId());

        return skillRequestMapper.toDto(skillRequest);
    }
}
