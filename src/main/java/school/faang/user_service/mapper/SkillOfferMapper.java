package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.dto.recommendation.SkillOfferDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillOfferMapper {
    @Mapping(target = "skillId", source = "skill.id")
    SkillOfferDto toDto(SkillOffer entity);

    SkillOffer toEntity(SkillOfferDto dto);
}
