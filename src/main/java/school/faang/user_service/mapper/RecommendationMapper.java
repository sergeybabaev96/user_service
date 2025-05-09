package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

@Mapper(componentModel = "spring")
public interface RecommendationMapper {

    RecommendationRequest toEntity(RecommendationRequestDto dto);

    RecommendationRequestDto toDto(RecommendationRequest entity);

    default SkillRequest map(String skillName) {
        SkillRequest skill = new SkillRequest();
        skill.getSkill().setTitle(skillName);
        return skill;
    }

    default String map(SkillRequest skill) {
        return skill.getSkill().getTitle();
    }
}