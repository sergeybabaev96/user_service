package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.dto.recommendation.SkillOfferDto;

@Mapper(componentModel = "spring")
public interface SkillOfferMapper {
    @Mapping(target = "skillId", source = "skill.id")
    @Mapping(target = "recommendationId", source = "recommendation.id")
    SkillOfferDto toDto(SkillOffer entity);

    @Mapping(target = "skill.id", source = "skillId")
    @Mapping(target = "recommendation.id", source = "recommendationId")
    SkillOffer toEntity(SkillOfferDto dto);
}
