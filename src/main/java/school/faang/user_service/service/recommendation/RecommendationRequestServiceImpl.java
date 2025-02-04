package school.faang.user_service.service.recommendation;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.request.RecommendationRequestDto;
import school.faang.user_service.dto.SkillRequestDto;
import school.faang.user_service.dto.recommendation.request.filter.RecommendationRequestFilter;
import school.faang.user_service.dto.recommendation.request.filter.RecommendationRequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.mapper.SkillRequestMapper;
import school.faang.user_service.properties.UserServiceProperties;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.service.skill.SkillRequestService;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RecommendationRequestServiceImpl implements RecommendationRequestService {
    private final UserServiceProperties userServiceProperties;
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final SkillRequestService skillRequestService;
    private final SkillRequestMapper skillRequestMapper;
    private final List<RecommendationRequestFilter> recommendationRequestFilters;

    @Override
    public RecommendationRequest create(RecommendationRequestDto dto) {
        checkRecommendationRequestDtoToSave(dto);

        RecommendationRequest recommendationRequest = recommendationRequestMapper.toEntity(dto);

        LocalDateTime now = LocalDateTime.now();
        recommendationRequest.setCreatedAt(now);
        recommendationRequest.setUpdatedAt(now);
        recommendationRequest.setStatus(RequestStatus.PENDING);
        recommendationRequest = recommendationRequestRepository.save(recommendationRequest);

        List<SkillRequestDto> savedSkillRqs = skillRequestService.createAllSkillRequest(dto.getSkills(), recommendationRequest);
        recommendationRequest.setSkills(savedSkillRqs.stream().map(skillRequestMapper::toEntity).toList());
        return recommendationRequest;
    }

    @Override
    public List<RecommendationRequestDto> getRequestByFilter(RecommendationRequestFilterDto dto) {
        List<RecommendationRequest> recommendationRequests = recommendationRequestRepository.findAll();

        return recommendationRequestFilters.stream()
                .filter(filter -> filter.isApplicable(dto))
                .flatMap(filter -> filter.apply(recommendationRequests.stream(), dto))
                .map(recommendationRequestMapper::toDto)
                .toList();
    }

    @Override
    public RecommendationRequest getRequestById(Long id) {
        return recommendationRequestRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("RecommendationRequest not found"));
    }

    private void checkRecommendationRequestDtoToSave(RecommendationRequestDto dto) {
        Set<Long> userIds = new HashSet<>(List.of(dto.getRequesterId(), dto.getReceiverId()));

        userIds.stream()
                .filter(userId -> !userRepository.existsById(userId))
                .forEach(userId -> {
                    throw new EntityNotFoundException("User with id " + userId + " not found");
                });

        LocalDateTime expireDate = LocalDateTime.now().minusMonths(userServiceProperties.getRecommendationRequest().getMinMonth());
        if (userIds.size() == 1 &&
                recommendationRequestRepository.isLatestPendingRequestCreatedAfterThenExists(dto.getRequesterId(), expireDate)) {
            throw new IllegalArgumentException("Less than min months have passed since the previous request");
        }

        if (dto.getSkills().stream().noneMatch(skillRepository::existsById)) {
            throw new EntityNotFoundException("Skills not found");
        }
    }
}
