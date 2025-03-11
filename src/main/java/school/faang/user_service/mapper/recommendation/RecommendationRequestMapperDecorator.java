package school.faang.user_service.mapper.recommendation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
public abstract class RecommendationRequestMapperDecorator implements RecommendationRequestMapper {
    @Autowired
    @Qualifier("delegate")
    private RecommendationRequestMapper delegate;


    @Autowired
    private SkillRepository skillRepository;

    @Override
    public List<SkillRequest> mapIdsToSkills(List<Long> skillIds) {
        if (skillIds == null || skillIds.isEmpty()) {
            return List.of();
        }

        List<Skill> skills = skillRepository.findAllById(skillIds);

        return skills.stream()
                .map(skill -> SkillRequest.builder()
                        .skill(skill)
                        .build())
                .collect(Collectors.toList());
    }
}
