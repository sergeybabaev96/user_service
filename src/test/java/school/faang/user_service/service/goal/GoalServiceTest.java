package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.constants.goal.GoalConstants;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.SearchGoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.goal.GoalAlreadyCompletedException;
import school.faang.user_service.exception.skill.SkillLimitExceededException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.goal.GoalMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {

    @InjectMocks
    private GoalService goalService;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SkillRepository skillRepository;

    @Spy
    private GoalMapperImpl goalMapper;

    @Mock
    private List<GoalFilter> goalFilters;

    private final Long userId = 1L;
    private final GoalDto goalDto = new GoalDto(
            1L, "description", null, "title", GoalStatus.ACTIVE, List.of(1L, 2L, 3L));
    private final Long goalId = 1L;
    private final SearchGoalDto searchGoalDto = new SearchGoalDto("title", GoalStatus.ACTIVE);

    @Mock
    private Stream<Goal> goalStream;

    @Test
    public void testNegativeCreateByDoesntExistUser() {
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> goalService.createGoal(userId, goalDto));
    }

    @Test
    public void testNegativeCreateByCountGoalsIsFull() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(goalRepository.findGoalsByUserId(userId)).thenReturn(goalStream);
        when(goalStream.count()).thenReturn((long) GoalConstants.MAX_COUNT_GOALS_PER_USER);

        assertThrows(SkillLimitExceededException.class, () -> goalService.createGoal(userId, goalDto));
    }

    @Test
    public void testNegativeCreateByDoesntExistSkills() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(goalRepository.findGoalsByUserId(userId)).thenReturn(goalStream);
        when(goalStream.count()).thenReturn(0L);
        Skill firstSkill = new Skill();
        firstSkill.setId(1L);
        Skill secondSkill = new Skill();
        secondSkill.setId(2L);
        when(skillRepository.findAllById(goalDto.skillIds())).thenReturn(List.of(firstSkill, secondSkill));

        assertThrows(IllegalArgumentException.class, () -> goalService.createGoal(userId, goalDto));
    }

    @Test
    public void testPositiveCreateWithSuccessSaveGoal() {
        Goal goalEntity = new Goal();
        goalEntity.setId(1L);
        goalEntity.setDescription("description");
        goalEntity.setTitle("title");
        goalEntity.setStatus(GoalStatus.ACTIVE);
        goalEntity.setSkillsToAchieve(List.of());
        Skill firstSkill = new Skill();
        firstSkill.setId(1L);
        Skill secondSkill = new Skill();
        secondSkill.setId(2L);
        Skill thirdSkill = new Skill();
        thirdSkill.setId(3L);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(goalRepository.findGoalsByUserId(userId)).thenReturn(goalStream);
        when(goalStream.count()).thenReturn(0L);
        when(skillRepository.findAllById(goalDto.skillIds())).thenReturn(List.of(firstSkill, secondSkill, thirdSkill));
        when(goalRepository.save(goalEntity)).thenReturn(goalEntity);

        goalService.createGoal(userId, goalDto);

        verify(goalRepository, times(1)).save(goalEntity);
    }

    @Test
    public void testNegativeDeleteWhenGoalNotExist() {
        when(goalRepository.existsById(goalId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> goalService.deleteGoal(goalId));
    }

    @Test
    public void testPositiveDeleteWhenGoalExist() {
        when(goalRepository.existsById(goalId)).thenReturn(true);

        goalService.deleteGoal(goalId);
    }


    @Test
    public void testNegativeUpdateWhenGoalNotExist() {
        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> goalService.updateGoal(goalId, goalDto));
    }

    @Test
    public void testNegativeUpdateWhenStatusIsCompleted() {
        Goal existingGoal = new Goal();
        existingGoal.setStatus(GoalStatus.COMPLETED);

        when(goalRepository.findById(goalId)).thenReturn(Optional.of(existingGoal));

        assertThrows(GoalAlreadyCompletedException.class, () -> goalService.updateGoal(goalId, goalDto));
    }

    @Test
    public void testNegativeUpdateByDoesntExistSkills() {
        Goal existingGoal = new Goal();
        existingGoal.setStatus(GoalStatus.ACTIVE);

        when(goalRepository.findById(goalId)).thenReturn(Optional.of(existingGoal));
        when(skillRepository.findAllById(goalDto.skillIds())).thenReturn(List.of());

        assertThrows(IllegalArgumentException.class, () -> goalService.updateGoal(goalId, goalDto));
    }


    public void testPositiveUpdateWithSuccessSaveGoal() {
        Goal existingGoal = new Goal();
        existingGoal.setStatus(GoalStatus.ACTIVE);

        Skill skill1 = new Skill();
        skill1.setId(1L);
        Skill skill2 = new Skill();
        skill2.setId(2L);
        Skill skill3 = new Skill();
        skill3.setId(3L);
        List<Skill> skills = List.of(skill1, skill2, skill3);

        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);
        List<User> users = List.of(user1, user2);

        when(goalRepository.findById(goalId)).thenReturn(Optional.of(existingGoal));
        when(skillRepository.findAllById(goalDto.skillIds())).thenReturn(skills);
        when(goalRepository.findUsersByGoalId(goalId)).thenReturn(users);

        goalService.updateGoal(goalId, goalDto);

        verify(goalRepository, times(1)).save(existingGoal);
        verify(userRepository, times(1)).save(user1);
        verify(userRepository, times(1)).save(user2);
        assertEquals(GoalStatus.COMPLETED, existingGoal.getStatus());
    }

    @Test
    public void testFindSubtasksByGoalIdNotExist() {
        when(goalRepository.existsById(goalId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> goalService.findSubtasksByGoalId(goalId, searchGoalDto));
    }


    public void testFindSubtasksByGoalIdExist() {
        Goal parentGoal = new Goal();
        parentGoal.setId(goalId);
        Goal goal1 = new Goal();
        goal1.setParent(parentGoal);
        goal1.setTitle("title");
        goal1.setStatus(GoalStatus.ACTIVE);
        Goal goal2 = new Goal();
        goal2.setParent(parentGoal);
        goal2.setTitle("name");
        goal2.setStatus(GoalStatus.ACTIVE);
        Stream<Goal> goals = Stream.of(goal1, goal2);
        when(goalRepository.existsById(goalId)).thenReturn(true);
        when(goalRepository.findByParent(goalId)).thenReturn(goals);
        when(goalFilters.get(0).isApplicable(any())).thenReturn(true);
        when(goalFilters.get(0).apply(goals, any())).thenReturn(Stream.of(goal1));

        List<GoalDto> filteredGoals = goalService.findSubtasksByGoalId(1L, searchGoalDto);
        assertEquals(1, filteredGoals.size());
    }

    @Test
    public void testGetGoalsByUserIdNotExist() {
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> goalService.getGoalsByUserId(goalId, searchGoalDto));
    }

    @Test
    public void testGetGoalsByUserIdExist() {

    }
}
