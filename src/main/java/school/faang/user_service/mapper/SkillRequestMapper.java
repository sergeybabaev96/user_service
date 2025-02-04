package school.faang.user_service.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import school.faang.user_service.dto.SkillRequestDto;
import school.faang.user_service.entity.recommendation.SkillRequest;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SkillRequestMapper {

    @Mapping(target = "requestId", source = "request.id")
    @Mapping(target = "skillId", source = "skill.id")
    SkillRequestDto toDto(SkillRequest skillRequest);

    @InheritInverseConfiguration
    SkillRequest toEntity(SkillRequestDto skillRequestDto);
}
