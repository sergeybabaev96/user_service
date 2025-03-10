package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.SkillRequestDto;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillRequestMapper {
    @Mapping(target = "skillId", source = "skill.id")
    SkillRequestDto toDto(SkillRequest entity);

    SkillRequest toEntity(SkillRequestDto dto);

    List<SkillRequest> toEntities(List<SkillRequestDto> dtos);

    @Mapping(target = "skillId", source = "skill.id")
    List<SkillRequestDto> toDtos(List<SkillRequest> entities);
}
