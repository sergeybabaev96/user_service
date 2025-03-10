package school.faang.user_service.service;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.SkillRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.recommendation.RecommendationRequestFilter;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.mapper.SkillRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    @Value("${recommendationRequestMinDistanceMonths}")
    private int recommendationRequestMinDistanceMonths;

    private final UserService userService;
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillRepository skillRepository;
    private final SkillRequestService skillRequestService;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final SkillRequestMapper skillRequestMapper;
    private final List<RecommendationRequestFilter> filters;

    public RecommendationRequestDto create(@NotNull RecommendationRequestDto recommendationRequest) {
        validateRecommendationRequest(recommendationRequest);

        var recommendationRequestEntity = recommendationRequestMapper.toEntity(recommendationRequest);
        recommendationRequestEntity.setRequester(userService.findById(recommendationRequest.getRequesterId()));
        recommendationRequestEntity.setReceiver(userService.findById(recommendationRequest.getReceiverId()));
        recommendationRequestEntity.setStatus(RequestStatus.PENDING);
        recommendationRequestEntity.setSkills(List.of());

        recommendationRequestEntity = recommendationRequestRepository.save(recommendationRequestEntity);
        var recommendationRequestId = recommendationRequestEntity.getId();

        assert recommendationRequest.getSkills() != null;
        recommendationRequest.getSkills()
                .forEach(skill -> skillRequestService.create(
                        recommendationRequestId,
                        skill.skillId()));

        var createdRecommendationRequest = recommendationRequestRepository.findById(recommendationRequestId)
                .orElseThrow(() -> new DataRetrievalFailureException(
                        "Recommendation request #%d is not created".formatted(recommendationRequestId)));

        var createdDto = recommendationRequestMapper.toDto(createdRecommendationRequest);
        createdDto.setSkills(recommendationRequest.getSkills());

        return createdDto;
    }

    public List<RecommendationRequestDto> getRequests(@NotNull RequestFilterDto filterDto) {
        var requests = recommendationRequestRepository.findAllWithSkills().stream();
        for (var filter : filters) {
            if (filter.isApplicable(filterDto)) {
                requests = filter.apply(requests, filterDto);
            }
        }

        return requests.map(entity -> {
                    var dto = recommendationRequestMapper.toDto(entity);
                    dto.setSkills(skillRequestMapper.toDtos(entity.getSkills()));

                    return dto;
                })
                .toList();
    }

    public RecommendationRequestDto getRequest(long id) {
        var entity = recommendationRequestRepository.findById(id)
                .orElseThrow(
                        () -> new DataRetrievalFailureException(
                                "Recommendation request with id %d is not found".formatted(id)));

        var dto = recommendationRequestMapper.toDto(entity);
        var skillRequests = skillRequestService.findAllByRequestId(dto.getId());
        dto.setSkills(skillRequestMapper.toDtos(skillRequests));

        return dto;
    }

    public boolean rejectRequest(long id, RejectionDto rejection) {
        var entity = recommendationRequestRepository.findById(id).orElseThrow(
                () -> new DataRetrievalFailureException(
                        "Recommendation request with id %d is not found".formatted(id)));
        if (entity.getStatus() == RequestStatus.ACCEPTED || entity.getStatus() == RequestStatus.REJECTED) {
            return false;
        }

        entity.setStatus(RequestStatus.REJECTED);
        entity.setRejectionReason(rejection.reason());
        entity.setUpdatedAt(LocalDateTime.now());

        recommendationRequestRepository.save(entity);

        return true;
    }

    private void validateRecommendationRequest(RecommendationRequestDto recommendationRequest) {
        var requesterId = recommendationRequest.getRequesterId();
        if (!userService.existsById(requesterId)) {
            throw new DataValidationException("Requester with id %d is not found".formatted(requesterId));
        }

        var receiverId = recommendationRequest.getReceiverId();
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
        if (recommendationRequest.getSkills() == null || recommendationRequest.getSkills().isEmpty()) {
            throw new DataValidationException("At least 1 skill must be send");
        }

        var existedSkillsInRecommendation = recommendationRequest.getSkills()
                .stream()
                .filter(dto -> skillRepository.existsById(dto.skillId()))
                .toList();
        if (existedSkillsInRecommendation.size() != recommendationRequest.getSkills().stream().distinct().count()) {
            throw new DataValidationException(
                    "Skills %s are not registered".formatted(
                            String.join(
                                    ", ",
                                    recommendationRequest.getSkills()
                                            .stream()
                                            .map(SkillRequestDto::skillId)
                                            .filter(skillId -> existedSkillsInRecommendation.stream()
                                                    .filter(
                                                            existedSkill -> existedSkill.skillId() == skillId)
                                                    .findFirst()
                                                    .isEmpty())
                                            .map(Object::toString)
                                            .toList())));
        }
    }
}
