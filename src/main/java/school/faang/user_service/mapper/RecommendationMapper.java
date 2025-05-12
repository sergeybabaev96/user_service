package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RecommendationResponseDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

@Mapper(componentModel = "spring")
public interface RecommendationMapper {

    RecommendationRequest toEntity(RecommendationRequestDto dto);

    RecommendationResponseDto toDto(RecommendationRequest entity);

    default SkillRequest map(String skillName) {
        SkillRequest skillRequest = new SkillRequest();
        Skill skill = new Skill();
        skill.setTitle(skillName);
        skillRequest.setSkill(skill);
        return skillRequest;
    }

    default String map(SkillRequest skill) {
        return skill.getSkill().getTitle();
    }
}