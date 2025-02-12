package school.faang.user_service.utils.goal;

import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.List;

public class GoalPrepareData {
    public static GoalDto getGoalDto() {
        return GoalDto.builder()
                .title("title")
                .description("descr")
                .skillIds(List.of())
                .build();
    }

    public static GoalDto getGoalDtoWithSkills() {
        return GoalDto.builder()
                .title("title")
                .description("descr")
                .skillIds(List.of(1L, 2L))
                .build();
    }

    public static Goal getGoalFromDto(GoalDto goalDto) {
        Goal goal = new Goal();
        goal.setTitle(goalDto.title());
        goal.setDescription(goalDto.description());
        return goal;
    }

    public static User getUserWithMaxActiveGoals(long userId) {
        User user = new User();
        user.setId(userId);
        user.setGoals(List.of(
                Goal.builder().status(GoalStatus.ACTIVE).build(),
                Goal.builder().status(GoalStatus.ACTIVE).build(),
                Goal.builder().status(GoalStatus.ACTIVE).build()
        ));
        return user;
    }

    public static User getUser(long userId) {
        User user = new User();
        user.setId(userId);
        user.setGoals(List.of(
                Goal.builder().status(GoalStatus.ACTIVE).build(),
                Goal.builder().status(GoalStatus.ACTIVE).build()
        ));
        return user;
    }

    public static GoalDto getExpectedGoalDto() {
        return new GoalDto("Updated Goal", "Updated Description", null, GoalStatus.ACTIVE, List.of(1L, 2L), null);
    }

    public static Goal getUpdatedGoal(long goalId, GoalDto goalDto) {
        Goal updatedGoal = new Goal();
        updatedGoal.setId(goalId);
        updatedGoal.setTitle(goalDto.title());
        updatedGoal.setDescription(goalDto.description());
        updatedGoal.setStatus(GoalStatus.ACTIVE);
        updatedGoal.setSkillsToAchieve(List.of(Skill.builder().id(1L).build(), Skill.builder().id(2L).build()));
        return updatedGoal;
    }

    public static Goal getExistingGoal(long goalId) {
        Goal existingGoal = new Goal();
        existingGoal.setId(goalId);
        existingGoal.setTitle("Old Goal");
        existingGoal.setDescription("Old Description");
        return existingGoal;
    }
}
