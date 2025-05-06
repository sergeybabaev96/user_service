package school.faang.user_service.mapper.goal;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.goal.GoalRequestDto;
import school.faang.user_service.entity.goal.Goal;
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalMapper {
    Goal toGoalEntity(final GoalRequestDto goalRequestDto);
}
