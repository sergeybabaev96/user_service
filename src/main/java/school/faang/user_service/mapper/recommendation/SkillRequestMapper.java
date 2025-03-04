package school.faang.user_service.mapper.recommendation;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.entity.recommendation.SkillRequest;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface SkillRequestMapper {
    @Mapping(source = "skill.id", target = "skillId")
    @Mapping(source = "skill.title", target = "skillTitle")
    @Mapping(source = "request.id", target = "recommendationRequestId")
    SkillRequestDto toDto(SkillRequest skillRequest);

    @Mapping(source = "skillId", target = "skill.id")
    @Mapping(source = "skillTitle", target = "skill.title")
    @Mapping(source = "recommendationRequestId", target = "request.id")
    @Mapping(target = "id", ignore = true)
    SkillRequest toEntity(SkillRequestDto skillRequestDto);
}
