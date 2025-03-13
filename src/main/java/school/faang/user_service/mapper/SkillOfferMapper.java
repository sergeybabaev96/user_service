package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;

@Mapper(componentModel = "spring")
public interface SkillOfferMapper {
    @Mapping(source = "skillId", target = "skill")
    @Mapping(source = "recommendationId", target = "recommendation")
    SkillOffer toEntity(SkillOfferDto skillOfferDto);
    @Mapping(source = "skill", target = "skillId")
    @Mapping(source = "recommendation", target = "recommendationId")
    SkillOfferDto toDto(SkillOffer skillOffer);

    default Skill mapSkill(Long skillId) {
        if (skillId == null) {
            return null;
        }
        Skill skill = new Skill();
        skill.setId(skillId);
        return skill;
    }

    default Long map(Skill skill) {
        return (skill == null) ? null : skill.getId();
    }

    default Recommendation mapRecommendation(Long recommendationId) {
        if (recommendationId == null) {
            return null;
        }
        Recommendation recommendation = new Recommendation();
        recommendation.setId(recommendationId);
        return recommendation;
    }

    default Long map(Recommendation recommendation) {
        return (recommendation == null) ? null : recommendation.getId();
    }
}