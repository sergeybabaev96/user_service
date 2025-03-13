package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
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

import java.util.Collections;
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

    private final Long userId = 1L;
    private final Long goalId = 1L;
    private final Goal existingGoal = new Goal();
    private final User firstUser = User.builder()
            .id(userId)
            .build();
    private final User secondUser = User.builder()
            .id(2L)
            .build();
    private final Skill firstSkill = Skill.builder()
            .id(1L)
            .build();
    private final Skill secondSkill = Skill.builder()
            .id(2L)
            .build();
    private final Skill thirdSkill = Skill.builder()
            .id(3L)
            .build();
    private final GoalDto firstGoalDto = GoalDto.builder()
            .id(goalId)
            .title("title")
            .status(GoalStatus.ACTIVE)
            .skillIds(List.of(1L, 2L, 3L))
            .build();
    private final GoalDto secondGoalDto = GoalDto.builder()
            .parentId(goalId)
            .title("title")
            .status(GoalStatus.ACTIVE)
            .skillIds(Collections.emptyList())
            .build();
    private final GoalDto thirdGoalDto = GoalDto.builder()
            .status(GoalStatus.COMPLETED)
            .skillIds(List.of(1L, 2L, 3L))
            .build();
    private final Goal goalEntity = Goal.builder()
            .id(goalId)
            .title("title")
            .status(GoalStatus.ACTIVE)
            .skillsToAchieve(List.of(firstSkill, secondSkill, thirdSkill))
            .users(List.of(firstUser))
            .build();
    private final Goal firstGoal = Goal.builder()
            .parent(goalEntity)
            .title("title")
            .status(GoalStatus.ACTIVE)
            .build();
    private final Goal secondGoal = Goal.builder()
            .parent(goalEntity)
            .title("title")
            .status(GoalStatus.COMPLETED)
            .build();
    private final Stream<Goal> goals = Stream.of(firstGoal, secondGoal);
    private final SearchGoalDto searchGoalDto = new SearchGoalDto("title", GoalStatus.ACTIVE);

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
    private GoalFilter goalFilterTitle;

    @Mock
    private GoalFilter goalFilterStatus;

    @Mock
    private Stream<Goal> goalStream;

    @BeforeEach
    public void setUp() {
        goalService = new GoalService(goalRepository, userRepository, skillRepository,
                goalMapper, List.of(goalFilterTitle, goalFilterStatus));
    }

    @Test
    public void testNegativeCreateByDoesntExistUser() {
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> goalService.createGoal(userId, firstGoalDto));
    }

    @Test
    public void testNegativeCreateByCountGoalsIsFull() {
        includeNegativeCreateByCountGoalIsFull();
        when(goalStream.count()).thenReturn((long) GoalConstants.MAX_COUNT_GOALS_PER_USER);

        assertThrows(SkillLimitExceededException.class, () -> goalService.createGoal(userId, firstGoalDto));
    }

    @Test
    public void testNegativeCreateByDoesntExistSkills() {
        includeNegativeCreateByCountGoalIsFull();
        when(goalStream.count()).thenReturn(0L);
        when(skillRepository.findAllById(firstGoalDto.skillIds())).thenReturn(List.of(firstSkill, secondSkill));

        assertThrows(IllegalArgumentException.class, () -> goalService.createGoal(userId, firstGoalDto));
    }

    @Test
    public void testPositiveCreateWithSuccessSaveGoal() {
        includeNegativeCreateByDoesntExistSkills();
        when(skillRepository.findAllById(firstGoalDto.skillIds()))
                .thenReturn(List.of(firstSkill, secondSkill, thirdSkill));
        when(userRepository.findById(userId)).thenReturn(Optional.of(firstUser));
        when(goalRepository.save(goalEntity)).thenReturn(goalEntity);

        goalService.createGoal(userId, firstGoalDto);

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

        verify(goalRepository, times(1)).deleteById(goalId);
    }

    @Test
    public void testNegativeUpdateWhenGoalNotExist() {
        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> goalService.updateGoal(goalId, firstGoalDto));
    }

    @Test
    public void testNegativeUpdateWhenStatusIsCompleted() {
        existingGoal.setStatus(GoalStatus.COMPLETED);
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(existingGoal));

        assertThrows(GoalAlreadyCompletedException.class, () -> goalService.updateGoal(goalId, firstGoalDto));
    }

    @Test
    public void testNegativeUpdateByDoesntExistSkills() {
        includeNegativeUpdateByDoesntExistSkills();
        when(skillRepository.findAllById(firstGoalDto.skillIds())).thenReturn(List.of());

        assertThrows(IllegalArgumentException.class, () -> goalService.updateGoal(goalId, firstGoalDto));
    }

    @Test
    public void testPositiveUpdateWithSuccessSaveGoal() {
        includeNegativeUpdateByDoesntExistSkills();
        List<Skill> skills = List.of(firstSkill, secondSkill, thirdSkill);
        List<User> users = List.of(firstUser, secondUser);
        when(skillRepository.findAllById(firstGoalDto.skillIds())).thenReturn(skills);
        when(goalRepository.findUsersByGoalId(goalId)).thenReturn(users);
        when(goalRepository.save(existingGoal)).thenReturn(existingGoal);

        goalService.updateGoal(goalId, thirdGoalDto);

        verify(goalRepository, times(1)).save(existingGoal);
        verify(userRepository, times(1)).save(firstUser);
        verify(userRepository, times(1)).save(secondUser);
        assertEquals(GoalStatus.COMPLETED, existingGoal.getStatus());
        assertEquals(existingGoal.getSkillsToAchieve(), skills);
        assertEquals(firstUser.getSkills(), skills);
        assertEquals(secondUser.getSkills(), skills);
    }

    @Test
    public void testNegativeFindSubtasksByGoalIdNotExist() {
        when(goalRepository.existsById(goalId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> goalService.findSubtasksByGoalId(goalId, searchGoalDto));
    }

    @Test
    public void testPositiveFindSubtasksByGoalIdExist() {
        when(goalRepository.existsById(goalId)).thenReturn(true);
        when(goalRepository.findByParent(goalId)).thenReturn(goals);
        when(goalFilterTitle.isApplicable(any())).thenReturn(true);
        when(goalFilterStatus.isApplicable(any())).thenReturn(true);
        when(goalFilterTitle.apply(any(), any())).thenReturn(Stream.of(firstGoal, secondGoal));
        when(goalFilterStatus.apply(any(), any())).thenReturn(Stream.of(firstGoal));

        List<GoalDto> filteredGoals = goalService.findSubtasksByGoalId(goalId, searchGoalDto);

        assertEquals(1, filteredGoals.size());
        assertEquals(secondGoalDto, filteredGoals.get(0));
    }

    @Test
    public void testNegativeGetGoalsByUserIdNotExist() {
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> goalService.getGoalsByUserId(goalId, searchGoalDto));
    }

    @Test
    public void testPositiveGetGoalsByUserIdExist() {
        List<User> users = List.of(firstUser);
        firstGoal.setUsers(users);
        secondGoal.setUsers(users);
        when(userRepository.existsById(userId)).thenReturn(true);
        when(goalRepository.findGoalsByUserId(userId)).thenReturn(goals);
        when(goalFilterTitle.isApplicable(any())).thenReturn(true);
        when(goalFilterStatus.isApplicable(any())).thenReturn(true);
        when(goalFilterTitle.apply(any(), any())).thenReturn(Stream.of(firstGoal, secondGoal));
        when(goalFilterStatus.apply(any(), any())).thenReturn(Stream.of(firstGoal));

        List<GoalDto> filteredGoals = goalService.getGoalsByUserId(userId, searchGoalDto);

        assertEquals(1, filteredGoals.size());
        assertEquals(secondGoalDto, filteredGoals.get(0));
    }

    private void includeNegativeCreateByCountGoalIsFull() {
        when(userRepository.existsById(userId)).thenReturn(true);
        when(goalRepository.findGoalsByUserId(userId)).thenReturn(goalStream);
    }

    private void includeNegativeCreateByDoesntExistSkills() {
        includeNegativeCreateByCountGoalIsFull();
        when(goalStream.count()).thenReturn(0L);
    }

    private void includeNegativeUpdateByDoesntExistSkills() {
        existingGoal.setStatus(GoalStatus.ACTIVE);
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(existingGoal));
    }
}
