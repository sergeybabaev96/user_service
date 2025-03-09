package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.dto.recommendation.SkillRequestDto;

@Mapper(componentModel = "spring")
public interface SkillRequestMapper {
    @Mapping(target = "skillId", source = "skill.id")
    @Mapping(target = "requestId", source = "request.id")
    SkillRequestDto toDto(SkillRequest entity);

    @Mapping(target = "request", ignore = true)
    @Mapping(target = "skill", ignore = true)
    SkillRequest toEntity(SkillRequestDto dto);
}
