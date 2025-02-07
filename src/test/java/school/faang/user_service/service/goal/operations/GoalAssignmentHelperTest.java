package school.faang.user_service.service.goal.operations;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalAssignmentHelperTest {

    @Mock
    private SkillRepository skillRepository;
    @Mock
    private GoalRepository goalRepository;

    private Goal goal;
    private List<Long> skillIds;
    private List<Skill> skills;
    private List<User> users;

    @InjectMocks
    private GoalAssignmentHelper goalAssignmentHelper;

    @BeforeEach
    void setup() {
        initTestData();
    }

    private void initTestData() {
        goal = new Goal();
        goal.setId(1L);
        goal.setSkillsToAchieve(new ArrayList<>());

        skillIds = List.of(1L, 2L, 3L);

        skills = skillIds.stream().map(id -> {
            Skill skill = new Skill();
            skill.setId(id);
            return skill;
        }).collect(Collectors.toList());

        User user1 = new User();
        user1.setSkills(new ArrayList<>());

        User user2 = new User();
        user2.setSkills(new ArrayList<>());

        users = List.of(user1, user2);
    }

    @Test
    void testAssignSkillsToGoal_AddsNewSkills() {
        when(skillRepository.findAllById(skillIds)).thenReturn(skills);

        goalAssignmentHelper.assignSkillsToGoal(goal, skillIds);

        verify(skillRepository).findAllById(skillIds);
        Assertions.assertTrue(goal.getSkillsToAchieve().containsAll(skills));
    }

    @Test
    void testAssignSkillsToGoal_DoesNothingIfSkillIdsNull() {
        goalAssignmentHelper.assignSkillsToGoal(goal, null);

        verify(skillRepository, never()).findAllById(anyList());
        Assertions.assertTrue(goal.getSkillsToAchieve().isEmpty());
    }

    @Test
    void testAssignSkillsToGoal_DoesNothingIfSkillIdsEmpty() {
        goalAssignmentHelper.assignSkillsToGoal(goal, List.of());

        verify(skillRepository, never()).findAllById(anyList());
        Assertions.assertTrue(goal.getSkillsToAchieve().isEmpty());
    }

    @Test
    void testAssignSkillsToUsers_AddsSkillsToUsers() {
        when(goalRepository.findUsersByGoalId(goal.getId())).thenReturn(users);
        when(skillRepository.findAllById(skillIds)).thenReturn(skills);

        goalAssignmentHelper.assignSkillsToUsers(goal, skillIds);

        verify(goalRepository).findUsersByGoalId(goal.getId());
        verify(skillRepository).findAllById(skillIds);

        Assertions.assertTrue(users.get(0).getSkills().containsAll(skills));
        Assertions.assertTrue(users.get(1).getSkills().containsAll(skills));
    }

    @Test
    void testAssignSkillsToUsers_DoesNothingIfNoUsers() {
        when(goalRepository.findUsersByGoalId(goal.getId())).thenReturn(List.of());

        goalAssignmentHelper.assignSkillsToUsers(goal, skillIds);

        verify(goalRepository).findUsersByGoalId(goal.getId());
        verify(skillRepository, never()).findAllById(anyList());
    }
}