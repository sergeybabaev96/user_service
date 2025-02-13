package school.faang.user_service.utils.goal;

import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;

import java.util.Arrays;
import java.util.List;

public class GoalPrepareData {
    public static GoalDto getGoalDto() {
        return GoalDto.builder()
                .id(1L)
                .title("title")
                .description("descr")
                .skillIds(Arrays.asList())
                .build();
    }

    public static GoalDto getGoalDtoWithSkills() {
        return GoalDto.builder()
                .title("title")
                .description("descr")
                .skillIds(Arrays.asList(1L, 2L))
                .build();
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
        user.setGoals(Arrays.asList(
                Goal.builder().status(GoalStatus.ACTIVE).build(),
                Goal.builder().status(GoalStatus.ACTIVE).build()
        ));
        return user;
    }

    public static GoalDto getExpectedGoalDto() {
        return GoalDto.builder()
                .id(1L)
                .title("Updated Goal")
                .description("Updated Description")
                .status( GoalStatus.ACTIVE)
                .skillIds(List.of(1L, 2L))
                .mentorId(2L)
                .build();
    }

    public static Goal getUpdatedGoal(long goalId) {
        Goal updatedGoal = new Goal();
        updatedGoal.setId(goalId);
        updatedGoal.setTitle("Updated Goal");
        updatedGoal.setDescription("Updated Description");
        updatedGoal.setStatus(GoalStatus.ACTIVE);
        updatedGoal.setSkillsToAchieve(Arrays.asList(Skill.builder().id(1L).build(), Skill.builder().id(2L).build()));
        updatedGoal.setMentor(User.builder().id(2L).build());
        return updatedGoal;
    }

    public static Goal getExistingGoal(long goalId) {
        Goal existingGoal = new Goal();
        existingGoal.setId(goalId);
        existingGoal.setTitle("Old Goal");
        existingGoal.setDescription("Old Description");
        existingGoal.setStatus(GoalStatus.ACTIVE);
        existingGoal.setMentor(User.builder().id(1L).build());
        existingGoal.setSkillsToAchieve(Arrays.asList(Skill.builder().id(1L).build(), Skill.builder().id(2L).build()));
        return existingGoal;
    }
}
