package school.faang.user_service.service;

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
import school.faang.user_service.exception.GoalAlreadyCompletedException;
import school.faang.user_service.exception.SkillLimitExceededException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.goal.GoalMapperImpl;
import school.faang.user_service.publisher.GoalCompletedPublisher;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.GoalService;

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

    private final Long firstId = 1L;
    private final Long secondId = 2L;
    private final Long thirdId = 3L;
    private final Goal existingGoal = new Goal();
    private final SearchGoalDto searchGoalDto = new SearchGoalDto("title", GoalStatus.ACTIVE);
    private final GoalDto firstGoalDto =
            createGoalDto(firstId, "title", GoalStatus.ACTIVE, List.of(firstId, secondId, thirdId), null);
    private final Goal goalEntity = createParentGoal();

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

    @Mock
    private GoalCompletedPublisher goalCompletedPublisher;

    @BeforeEach
    public void setUp() {
        goalService = new GoalService(goalRepository, userRepository, skillRepository,
                goalMapper, List.of(goalFilterTitle, goalFilterStatus), goalCompletedPublisher);
    }

    @Test
    public void testNegativeCreateByDoesntExistUser() {
        when(userRepository.existsById(firstId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> goalService.createGoal(firstId, firstGoalDto));
    }

    @Test
    public void testNegativeCreateByCountGoalsIsFull() {
        includeNegativeCreateByCountGoalIsFull();
        when(goalStream.count()).thenReturn((long) GoalConstants.MAX_COUNT_GOALS_PER_USER);

        assertThrows(SkillLimitExceededException.class, () -> goalService.createGoal(firstId, firstGoalDto));
    }

    @Test
    public void testNegativeCreateByDoesntExistSkills() {
        includeNegativeCreateByCountGoalIsFull();
        when(goalStream.count()).thenReturn(0L);
        when(skillRepository.findAllById(firstGoalDto.skillIds())).thenReturn(
                List.of(createSkill(firstId), createSkill(secondId)));

        assertThrows(IllegalArgumentException.class, () -> goalService.createGoal(firstId, firstGoalDto));
    }

    @Test
    public void testPositiveCreateWithSuccessSaveGoal() {
        includeNegativeCreateByDoesntExistSkills();
        when(skillRepository.findAllById(firstGoalDto.skillIds()))
                .thenReturn(createSkills());
        when(userRepository.findById(firstId)).thenReturn(Optional.of(createUser(firstId)));
        when(goalRepository.save(goalEntity)).thenReturn(goalEntity);

        goalService.createGoal(firstId, firstGoalDto);

        verify(goalRepository, times(1)).save(goalEntity);
    }

    @Test
    public void testNegativeDeleteWhenGoalNotExist() {
        when(goalRepository.existsById(firstId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> goalService.deleteGoal(firstId));
    }

    @Test
    public void testPositiveDeleteWhenGoalExist() {
        when(goalRepository.existsById(firstId)).thenReturn(true);

        goalService.deleteGoal(firstId);

        verify(goalRepository, times(1)).deleteById(firstId);
    }

    @Test
    public void testNegativeUpdateWhenGoalNotExist() {
        when(goalRepository.findById(firstId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> goalService.updateGoal(firstId, firstGoalDto));
    }

    @Test
    public void testNegativeUpdateWhenStatusIsCompleted() {
        existingGoal.setStatus(GoalStatus.COMPLETED);
        when(goalRepository.findById(firstId)).thenReturn(Optional.of(existingGoal));

        assertThrows(GoalAlreadyCompletedException.class, () -> goalService.updateGoal(firstId, firstGoalDto));
    }

    @Test
    public void testNegativeUpdateByDoesntExistSkills() {
        includeNegativeUpdateByDoesntExistSkills();
        when(skillRepository.findAllById(firstGoalDto.skillIds())).thenReturn(List.of());

        assertThrows(IllegalArgumentException.class, () -> goalService.updateGoal(firstId, firstGoalDto));
    }

    @Test
    public void testPositiveUpdateWithSuccessSaveGoal() {
        includeNegativeUpdateByDoesntExistSkills();
        User firstUser = createUser(firstId);
        User secondUser = createUser(secondId);
        List<Skill> skills = createSkills();
        List<Long> usersIds = List.of(firstUser.getId(), secondUser.getId());
        GoalDto goalDto = createGoalDto(null, null, GoalStatus.COMPLETED,
                List.of(firstId, secondId, thirdId), null);
        when(skillRepository.findAllById(firstGoalDto.skillIds())).thenReturn(skills);
        when(goalRepository.findUserIdsByGoalId(firstId)).thenReturn(usersIds);
        when(goalRepository.save(existingGoal)).thenReturn(existingGoal);

        goalService.updateGoal(firstId, goalDto);

        verify(goalRepository, times(1)).save(existingGoal);
        assertEquals(GoalStatus.COMPLETED, existingGoal.getStatus());
        assertEquals(existingGoal.getSkillsToAchieve(), skills);
    }

    @Test
    public void testNegativeFindSubtasksByGoalIdNotExist() {
        when(goalRepository.existsById(firstId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> goalService.findSubtasksByGoalId(firstId, searchGoalDto));
    }

    @Test
    public void testPositiveFindSubtasksByGoalIdExist() {
        Goal firstGoal = createSubGoal(goalEntity, "title", GoalStatus.ACTIVE);
        Goal secondGoal = createSubGoal(goalEntity, "title", GoalStatus.COMPLETED);
        Stream<Goal> goals = Stream.of(firstGoal, secondGoal);

        when(goalRepository.existsById(firstId)).thenReturn(true);
        when(goalRepository.findByParent(firstId)).thenReturn(goals);
        when(goalFilterTitle.isApplicable(any())).thenReturn(true);
        when(goalFilterStatus.isApplicable(any())).thenReturn(true);
        when(goalFilterTitle.apply(any(), any())).thenReturn(Stream.of(firstGoal, secondGoal));
        when(goalFilterStatus.apply(any(), any())).thenReturn(Stream.of(firstGoal));

        List<GoalDto> filteredGoals = goalService.findSubtasksByGoalId(firstId, searchGoalDto);

        assertEquals(1, filteredGoals.size());
        assertEquals(createGoalDto(null, "title", GoalStatus.ACTIVE, Collections.emptyList(), firstId),
                filteredGoals.get(0));
    }

    @Test
    public void testNegativeGetGoalsByUserIdNotExist() {
        when(userRepository.existsById(firstId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> goalService.getGoalsByUserId(firstId, searchGoalDto));
    }

    @Test
    public void testPositiveGetGoalsByUserIdExist() {
        Goal firstGoal = createSubGoal(goalEntity, null, GoalStatus.ACTIVE);
        Goal secondGoal = createSubGoal(goalEntity, null, GoalStatus.COMPLETED);
        Stream<Goal> goals = Stream.of(firstGoal, secondGoal);

        List<User> users = List.of(createUser(firstId));
        firstGoal.setUsers(users);
        secondGoal.setUsers(users);
        when(userRepository.existsById(firstId)).thenReturn(true);
        when(goalRepository.findGoalsByUserId(firstId)).thenReturn(goals);
        when(goalFilterTitle.isApplicable(any())).thenReturn(true);
        when(goalFilterStatus.isApplicable(any())).thenReturn(true);
        when(goalFilterTitle.apply(any(), any())).thenReturn(Stream.of(firstGoal, secondGoal));
        when(goalFilterStatus.apply(any(), any())).thenReturn(Stream.of(firstGoal));

        List<GoalDto> filteredGoals = goalService.getGoalsByUserId(firstId, searchGoalDto);

        assertEquals(1, filteredGoals.size());
        assertEquals(createGoalDto(null, null, GoalStatus.ACTIVE, Collections.emptyList(), firstId),
                filteredGoals.get(0));
    }

    private void includeNegativeCreateByCountGoalIsFull() {
        when(userRepository.existsById(firstId)).thenReturn(true);
        when(goalRepository.findGoalsByUserId(firstId)).thenReturn(goalStream);
    }

    private void includeNegativeCreateByDoesntExistSkills() {
        includeNegativeCreateByCountGoalIsFull();
        when(goalStream.count()).thenReturn(0L);
    }

    private void includeNegativeUpdateByDoesntExistSkills() {
        existingGoal.setStatus(GoalStatus.ACTIVE);
        when(goalRepository.findById(firstId)).thenReturn(Optional.of(existingGoal));
    }

    private User createUser(Long id) {
        return User.builder()
                .id(id)
                .build();
    }

    private Skill createSkill(Long id) {
        return Skill.builder()
                .id(id)
                .build();
    }

    private GoalDto createGoalDto(Long id, String title, GoalStatus status, List<Long> skillIds, Long parentId) {
        return GoalDto.builder()
                .id(id)
                .title(title)
                .status(status)
                .skillIds(skillIds)
                .parentId(parentId)
                .build();
    }

    private Goal createParentGoal() {
        return Goal.builder()
                .id(firstId)
                .title("title")
                .status(GoalStatus.ACTIVE)
                .skillsToAchieve(List.of(createSkill(firstId), createSkill(secondId), createSkill(thirdId)))
                .users(List.of(createUser(firstId)))
                .build();
    }

    private Goal createSubGoal(Goal parent, String title, GoalStatus status) {
        return Goal.builder()
                .parent(parent)
                .title(title)
                .status(status)
                .build();
    }

    private List<Skill> createSkills() {
        return List.of(createSkill(firstId), createSkill(secondId), createSkill(thirdId));
    }
}
