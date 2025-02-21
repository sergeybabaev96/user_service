package school.faang.user_service.mapper.goal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalMapper {

    @Mapping(target = "parent.id", source = "parent")
    @Mapping(target = "mentor.id", ignore = true)
    Goal toEntity(GoalDto goalDto);

    @Mapping(target = "parent", source = "parent.id")
    @Mapping(target = "mentorId", source = "mentor.id")
    GoalDto toDto(Goal goal);

    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Goal update(GoalDto dto, @MappingTarget Goal goal);

}
