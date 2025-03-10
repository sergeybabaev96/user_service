package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.repository.SkillRepository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillMapperService {
    private final SkillRepository skillRepository;

    public List<SkillRequest> mapIdsToSkills(List<Long> skillIds) {
        if (skillIds == null || skillIds.isEmpty()) {
            return List.of();
        }

        Map<Long, Skill> skillsMap = skillRepository.findAllById(skillIds)
                .stream()
                .collect(Collectors.toMap(Skill::getId, Function.identity()));

        return skillIds.stream()
                .map(skillId -> {
                    Skill skill = skillsMap.get(skillId);
                    if (skill == null) {
                        throw new IllegalArgumentException("Skill with id " + skillId + " not found");
                    }
                    return SkillRequest.builder()
                            .skill(skill)
                            .build();
                })
                .toList();
    }
}
