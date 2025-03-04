package school.faang.user_service.service.recommendation;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.service.recommendation.filter.RecommendationRequestFilter;
import school.faang.user_service.validator.recommendation.RecommendationRequestValidator;
import school.faang.user_service.validator.user.UserValidator;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final UserValidator userValidator;
    private final RecommendationRequestValidator recommendationRequestValidator;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final SkillRepository skillRepository;
    private final List<RecommendationRequestFilter> recommendationRequestFilters;

    public RecommendationRequestDto create(@NotNull RecommendationRequestDto recommendationRequestDto) {
        log.info("Creating a recommendations request from user with id {} for user with id {}",
                recommendationRequestDto.getReceiverId(), recommendationRequestDto.getRequesterId());

        userValidator.validateUser(recommendationRequestDto.getRequesterId());
        userValidator.validateUser(recommendationRequestDto.getReceiverId());
        recommendationRequestValidator.validateRecommendation(recommendationRequestDto);


        RecommendationRequest request = recommendationRequestMapper.toEntity(recommendationRequestDto);

        RecommendationRequest finalRequest = request;
        List<SkillRequest> skillRequest = recommendationRequestDto.getSkillRequests().stream()
                .map(skillRequestDto -> {
            Skill skill = getSkill(skillRequestDto.getSkillId());
            return SkillRequest.builder().request(finalRequest).skill(skill).build();
        }).toList();

        request.setSkills(skillRequest);

        request = recommendationRequestRepository.save(request);
        log.info("Recommendation request with id {} successfully saved", request.getId());
        return recommendationRequestMapper.toDto(request);
    }

    public List<RecommendationRequestDto> getRequests(RequestFilterDto requestFilter) {
        Stream<RecommendationRequest> recommendationRequests = recommendationRequestRepository.findAll().stream();
        recommendationRequestFilters.stream().filter(filter -> filter.isApplicable(requestFilter)).forEach(filter ->
                filter.apply(recommendationRequests, requestFilter));
        log.info("Getting a list of recommendation requests after filtering");
        return recommendationRequestMapper.toDtoList(recommendationRequests.toList());
    }

    public RecommendationRequestDto getRequest(long id) {
        RecommendationRequest entity = recommendationRequestRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Request with " + id + " not found"));

        return recommendationRequestMapper.toDto(entity);
    }

    public RecommendationRequestDto rejectRequest(Long id, @NotNull RejectionDto rejectionDto) {
        RecommendationRequest recommendationRequest = recommendationRequestValidator.validateRecommendationFromBd(id);
        recommendationRequestValidator.checkRequestsStatus(id, recommendationRequest.getStatus());
        recommendationRequest.setStatus(RequestStatus.REJECTED);
        recommendationRequest.setRejectionReason(rejectionDto.getReason());
        recommendationRequestRepository.save(recommendationRequest);
        log.info("Recommendation request with id {} was rejected", id);
        return recommendationRequestMapper.toDto(recommendationRequest);
    }

    private Skill getSkill(Long skillId) {
        return skillRepository.findById(skillId).orElseThrow(() -> {
            log.warn("Skill with id {} not found", skillId);
            return new NoSuchElementException(String.format("There is no skill with id = %d", skillId));
        });
    }


}
