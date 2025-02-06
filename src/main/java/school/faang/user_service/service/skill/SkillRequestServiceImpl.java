package school.faang.user_service.service.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.SkillRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.SkillRequestMapper;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillRequestServiceImpl implements SkillRequestService {
    private final SkillRequestRepository skillRequestRepository;
    private final SkillService skillService;
    private final SkillRequestMapper skillRequestMapper;

    @Override
    public List<SkillRequestDto> createAllSkillRequest(List<Long> skillIds, RecommendationRequest recommendationRequest) {
        List<SkillRequest> skillRequests = skillIds.stream().map(skillId -> {
                    SkillRequest.SkillRequestBuilder builder = SkillRequest.builder();
                    return builder.request(recommendationRequest)
                    .skill(skillService.getSkillById(skillId))
                    .build();
                })
                .toList();
        List<SkillRequest> saved = (List<SkillRequest>) skillRequestRepository.saveAll(skillRequests);
        return saved.stream().map(skillRequestMapper::toDto).toList();
    }
}
