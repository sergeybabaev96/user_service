package school.faang.user_service.mapper.goal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.goal.CreateGoalRequestDto;
import school.faang.user_service.dto.goal.CreateGoalResponse;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.UpdateGoalRequestDto;
import school.faang.user_service.dto.goal.UpdateGoalResponse;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.mapper.SkillMapper;

@Mapper(componentModel = "spring", uses = {SkillMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalMapper {

    GoalDto toDto(Goal goal);

    Goal toEntity(GoalDto goalDto);

    @Mapping(source = "parentId", target = "parent.id")
    @Mapping(source = "skillIds", target = "skillsToAchieve", qualifiedByName = "mapIdsToSkills")
    Goal toEntity(CreateGoalRequestDto request);

    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(source = "skillsToAchieve", target = "skillIds", qualifiedByName = "mapSkillsToIds")
    @Mapping(source = "createdAt", target = "createdAt")
    CreateGoalResponse toCreateResponse(Goal goal);

    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(source = "skillsToAchieve", target = "skillIds", qualifiedByName = "mapSkillsToIds")
    @Mapping(source = "updatedAt", target = "updatedAt")
    UpdateGoalResponse toUpdateResponse(Goal goal);

    @Mapping(source = "parentId", target = "parent.id")
    @Mapping(source = "skillIds", target = "skillsToAchieve", qualifiedByName = "mapIdsToSkills")
    void updateGoalFromDto(UpdateGoalRequestDto request, @MappingTarget Goal goal);
}