package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.filter.request.RecommendationRequestFilter;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.validator.RequestValidation;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RecommendationRequestService {

    private final RecommendationRequestRepository requestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final RequestValidation requestValidation;
    private final SkillRequestRepository skillRequestRepository;
    private final List<RecommendationRequestFilter> recommendationRequestFilters;

    public RecommendationRequestDto create(RecommendationRequestDto dto) {

        List<Skill> skills = requestValidation.validateRequest(dto);

        RecommendationRequest request = recommendationRequestMapper.toEntity(dto);
        request = requestRepository.save(request);

        saveSkillRequests(request, skills);
        return recommendationRequestMapper.toDto(request);
    }

    private void saveSkillRequests(RecommendationRequest request, List<Skill> skills) {

        for (Skill skill : skills) {
            SkillRequest skillRequest = new SkillRequest();
            skillRequest.setRequest(request);
            skillRequest.setSkill(skill);
            skillRequestRepository.save(skillRequest);
        }
    }

    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejection) {
        RecommendationRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recommendation request not found"));
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Cannot reject a non pending request");
        }

        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejection.getReason());
        request = requestRepository.save(request);
        return recommendationRequestMapper.toDto(request);
    }

    public RecommendationRequestDto requestRecommendation(RecommendationRequestDto recommendationRequest) {

        requestValidation.validateRequest(recommendationRequest);
        recommendationRequest.setStatus(RequestStatus.PENDING);
        recommendationRequest.setCreatedAt(LocalDateTime.now());
        recommendationRequest.setUpdatedAt(LocalDateTime.now());

        return create(recommendationRequest);
    }

    public List<RecommendationRequestDto> getRecommendationRequests(RequestFilterDto filters) {
        List<RecommendationRequest> requests = requestRepository.findAll();
        List<RecommendationRequestFilter> applicableFilters = recommendationRequestFilters.stream()
                .filter(filter -> filter.isApplicable(filters)).toList();

        if (applicableFilters.isEmpty()) {
            return List.of();
        }

        return applicableFilters.stream()
                .reduce(requests.stream(),
                        (requestStream, filter) -> filter.apply(requestStream, filters),
                        (list1, list2) -> list1)
                .map(recommendationRequestMapper::toDto).toList();
    }

    public RecommendationRequestDto getRecommendationRequests(long id) {
        RecommendationRequest request = findRequestById(id);
        return recommendationRequestMapper.toDto(request);
    }

    private RecommendationRequest findRequestById(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("запрос на рекомендацию с ID {} не найден", id);
                    return new EntityNotFoundException("Recommendation request not found");
                });
    }
}
