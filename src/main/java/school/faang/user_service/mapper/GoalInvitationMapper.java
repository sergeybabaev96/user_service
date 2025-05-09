package school.faang.user_service.mapper;

import org.mapstruct.*;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.service.GoalService;
import school.faang.user_service.service.UserService;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalInvitationMapper {

    @Mapping(source = "goal", target = "goalId")
    @Mapping(source = "inviter", target = "inviterId")
    @Mapping(source = "invited", target = "invitedUserId")
    GoalInvitationDto gInvitationToGIDTO(GoalInvitation createdInvitation);

    @Mapping(source = "inviterId", target = "inviter", qualifiedByName = "mapInviter")
    @Mapping(source = "invitedUserId", target = "invited", qualifiedByName = "mapInvited")
    @Mapping(source = "goalId", target = "goal", qualifiedByName = "mapGoal")
    GoalInvitation gIDTOToGoalInvitation(GoalInvitationDto goalInvitationDto,
                                         @Context GoalService goalService,
                                         @Context UserService userService);

    @Named("mapInviter")
    default User mapInviter(Long id, @Context UserService userService) {
        return id != null ? userService.findById(id) : null;
    }
    @Named("mapInvited")
    default User mapInvited(Long id, @Context UserService userService) {
        return id != null ? userService.findById(id) : null;
    }

    @Named("mapGoal")
    default Goal mapGoal(Long id, @Context GoalService goalService) {
        return id != null ? goalService.findById(id) : null;
    }

}
