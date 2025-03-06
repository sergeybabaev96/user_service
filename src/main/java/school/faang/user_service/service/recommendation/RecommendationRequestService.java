package school.faang.user_service.service.recommendation;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.filter.request.RecommendationRequestFilter;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.validator.recommendation.RequestValidation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final RequestValidation requestValidation;
    private final SkillRequestRepository skillRequestRepository;
    private final List<RecommendationRequestFilter> recommendationRequestFilters;

    @Transactional
    public RecommendationRequestDto requestRecommendation(RecommendationRequestDto recommendationRequest) {

        requestValidation.validateRequest(recommendationRequest);
        recommendationRequest.setStatus(RequestStatus.PENDING);
        recommendationRequest.setCreatedAt(LocalDateTime.now());
        recommendationRequest.setUpdatedAt(LocalDateTime.now());

        return create(recommendationRequest);
    }

    private RecommendationRequestDto create(RecommendationRequestDto dto) {

        List<Skill> skills = requestValidation.validateRequest(dto);

        RecommendationRequest request = recommendationRequestMapper.toEntity(dto);
        request = recommendationRequestRepository.save(request);

        saveSkillRequests(request, skills);
        return recommendationRequestMapper.toDto(request);
    }

    private void saveSkillRequests(RecommendationRequest request, List<Skill> skills) {
        long requestId = request.getId();
        skills.forEach(skill -> {
            long skillId = skill.getId();
            skillRequestRepository.create(requestId, skillId);
        });
    }

    public List<RecommendationRequestDto> getRecommendationRequests(RequestFilterDto filters) {
        List<RecommendationRequest> requests = recommendationRequestRepository.findAll();
        return recommendationRequestFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .reduce(requests.stream(),
                        (requestStream, filter) -> filter.apply(requestStream, filters),
                        Stream::concat)
                .map(recommendationRequestMapper::toDto)
                .toList();
    }

    public RecommendationRequestDto getRecommendationRequest(long id) {
        RecommendationRequest request = findRequestById(id);
        return recommendationRequestMapper.toDto(request);
    }

    private RecommendationRequest findRequestById(Long id) {
        return recommendationRequestRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Recommendation request with ID {} not found", id);
                    return new EntityNotFoundException("Recommendation request not found");
                });
    }

    @Transactional
    public RecommendationRequestDto rejectRequest(long id, RejectionDto rejection) {
        RecommendationRequest request = recommendationRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recommendation request not found"));
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Cannot reject a non pending request");
        }

        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejection.getReason());
        request.setUpdatedAt(LocalDateTime.now());
        request = recommendationRequestRepository.save(request);
        return recommendationRequestMapper.toDto(request);
    }

}
