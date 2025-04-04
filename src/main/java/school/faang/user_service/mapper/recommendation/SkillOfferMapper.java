package school.faang.user_service.mapper.recommendation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.SkillOffer;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillOfferMapper {

    @Mapping(target = "skill", ignore = true)
    SkillOffer toEntity(SkillOfferDto skillOfferDto);

    @Mapping(source = "skill.id", target = "skillId")
    SkillOfferDto toDto(SkillOffer skillOffer);
}
