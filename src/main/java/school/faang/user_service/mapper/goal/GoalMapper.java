package school.faang.user_service.mapper.goal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.goal.Goal;

@Mapper(componentModel = "spring")
public interface GoalMapper {

    GoalDto toDto(Goal goal);

    @Mapping(target = "parent", ignore = true)
    Goal toEntity(GoalDto goalDto);

}
