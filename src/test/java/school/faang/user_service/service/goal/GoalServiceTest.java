package school.faang.user_service.service.goal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import school.faang.user_service.dto.goal.CreateGoalRequestDto;
import school.faang.user_service.dto.goal.CreateGoalResponse;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.UpdateGoalRequestDto;
import school.faang.user_service.dto.goal.UpdateGoalResponse;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.goal.data.GoalDataFilter;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.operations.GoalAssignmentHelper;
import school.faang.user_service.service.goal.operations.GoalValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;
    @Mock
    private GoalValidator goalValidator;
    @Mock
    private GoalAssignmentHelper goalAssignmentHelper;
    @Mock
    private GoalDataFilter goalDataFilter1;
    @Mock
    private GoalDataFilter goalDataFilter2;
    @Mock
    private GoalDataFilter goalDataFilter3;

    @Spy
    private GoalMapper goalMapper;

    private Long userId;
    private Long goalId;
    private Goal goal;
    private Goal existingGoal;
    private CreateGoalRequestDto createGoalRequestDto;
    private CreateGoalResponse createGoalResponse;
    private UpdateGoalRequestDto updateGoalRequestDto;
    private UpdateGoalResponse updateGoalResponse;
    private GoalFilterDto goalFilterDto;
    private List<GoalDataFilter> goalDataFilters;

    @InjectMocks
    private GoalService goalService;

    @BeforeEach
    void setup() {
        userId = 1L;
        goalId = 1L;

        goal = new Goal();
        goal.setId(goalId);
        goal.setTitle("Goal Title");
        goal.setDescription("Goal Description");
        goal.setStatus(GoalStatus.ACTIVE);
        goal.setSkillsToAchieve(new ArrayList<>());
        goal.setParent(new Goal());
        goal.getParent().setId(2L);

        existingGoal = new Goal();
        existingGoal.setId(goalId);
        existingGoal.setTitle("Existing Goal");
        existingGoal.setDescription("Existing Description");
        existingGoal.setStatus(GoalStatus.ACTIVE);
        existingGoal.setSkillsToAchieve(new ArrayList<>());

        createGoalRequestDto = new CreateGoalRequestDto();
        createGoalRequestDto.setUserId(userId);
        createGoalRequestDto.setTitle("New Goal");
        createGoalRequestDto.setDescription("New Goal Description");
        createGoalRequestDto.setStatus(GoalStatus.ACTIVE);
        createGoalRequestDto.setSkillIds(List.of(1L, 2L, 3L));

        createGoalResponse = new CreateGoalResponse();
        createGoalResponse.setId(goalId);
        createGoalResponse.setTitle("New Goal");
        createGoalResponse.setStatus(GoalStatus.ACTIVE);
        createGoalResponse.setDescription("New Goal Description");

        updateGoalRequestDto = new UpdateGoalRequestDto();
        updateGoalRequestDto.setGoalId(goalId);
        updateGoalRequestDto.setTitle("Updated Goal");
        updateGoalRequestDto.setDescription("Updated Goal Description");
        updateGoalRequestDto.setStatus(GoalStatus.ACTIVE);

        updateGoalResponse = new UpdateGoalResponse();
        updateGoalResponse.setId(goalId);
        updateGoalResponse.setTitle("Updated Goal");
        updateGoalResponse.setStatus(GoalStatus.ACTIVE);
        updateGoalResponse.setDescription("Updated Goal Description");

        goalFilterDto = new GoalFilterDto();
        goalFilterDto.setTitle("Filtered Goal");

        goalDataFilters = List.of(goalDataFilter1, goalDataFilter2, goalDataFilter3);
        ReflectionTestUtils.setField(goalService, "goalDataFilters", goalDataFilters);
    }

    @Test
    void testCreateGoal_ShouldValidateAndSave() {
        when(goalMapper.toEntity(createGoalRequestDto)).thenReturn(goal);
        when(goalRepository.save(any(Goal.class))).thenReturn(goal);
        when(goalMapper.toCreateResponse(any(Goal.class))).thenReturn(createGoalResponse);

        final CreateGoalResponse response = goalService.createGoal(createGoalRequestDto);

        verify(goalValidator).validateActiveGoalsLimit(userId);
        verify(goalValidator).validateSkillsExist(createGoalRequestDto.getSkillIds());
        verify(goalRepository).save(goal);
        verify(goalAssignmentHelper).assignSkillsToGoal(goal, createGoalRequestDto.getSkillIds());
        Assertions.assertEquals(createGoalResponse, response);
    }

    @Test
    void testUpdateGoal_ShouldValidateAndUpdate() {
        updateGoalRequestDto.setGoalId(goalId);
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(existingGoal));
        when(goalMapper.toUpdateResponse(existingGoal)).thenReturn(updateGoalResponse);

        final UpdateGoalResponse response = goalService.updateGoal(updateGoalRequestDto);

        verify(goalValidator).validateGoalUpdatable(existingGoal);
        verify(goalValidator).validateSkillsExist(updateGoalRequestDto.getSkillIds());
        verify(goalMapper).updateGoalFromDto(updateGoalRequestDto, existingGoal);
        verify(goalRepository).save(existingGoal);
        verify(goalAssignmentHelper).assignSkillsToGoal(existingGoal, updateGoalRequestDto.getSkillIds());
        Assertions.assertEquals(updateGoalResponse, response);
    }

    @Test
    void testUpdateGoal_ShouldAssignSkillsWhenCompleted() {
        updateGoalRequestDto.setGoalId(goalId);
        updateGoalRequestDto.setStatus(GoalStatus.COMPLETED);
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(existingGoal));

        goalService.updateGoal(updateGoalRequestDto);

        verify(goalAssignmentHelper).assignSkillsToUsers(existingGoal, updateGoalRequestDto.getSkillIds());
    }

    @Test
    void testDeleteGoal_ShouldFindAndDelete() {
        when(goalRepository.findById(goalId)).thenReturn(Optional.of(existingGoal));

        goalService.deleteGoal(goalId);

        verify(goalRepository).delete(existingGoal);
    }

    @Test
    void testFindSubtasksByGoalId_CallsRepository() {
        when(goalRepository.findByParent(goalId)).thenReturn(Stream.of(goal));

        goalService.findSubtasksByGoalId(goalId, goalFilterDto);

        verify(goalRepository).findByParent(goalId);
    }

    @Test
    void testUpdateGoal_ShouldThrowExceptionIfGoalNotFound() {
        updateGoalRequestDto.setGoalId(goalId);
        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalService.updateGoal(updateGoalRequestDto));

        Assertions.assertEquals("Goal not found with id: " + goalId, exception.getMessage());
    }

    @Test
    void testDeleteGoal_ShouldThrowExceptionIfGoalNotFound() {
        when(goalRepository.findById(goalId)).thenReturn(Optional.empty());

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> goalService.deleteGoal(goalId));

        Assertions.assertEquals("Goal not found with id: " + goalId, exception.getMessage());
    }

    @Test
    void testGetGoalsByUser_CallsRepository() {
        when(goalRepository.findGoalsByUserId(userId)).thenReturn(Stream.of(goal));

        goalService.getGoalsByUser(userId, goalFilterDto);

        verify(goalRepository).findGoalsByUserId(userId);
    }
  
    @Test
    void testStopGoalsByUser_ShouldRemoveUserFromParticipantsList() {
        User user1 = User.builder().id(1L).build();
        User user2 = User.builder().id(2L).build();
        Stream<Goal> goals = Stream.of(Goal.builder()
                .users(new ArrayList<>(List.of(user1, user2)))
                .build());
        when(goalRepository.findGoalsByUserId(1L)).thenReturn(goals);

        List<User> expected = new ArrayList<>(List.of(user2));

        List<User> actual = goalService.stopGoalsByUser(1L).get(0).getUsers();

        assertEquals(expected, actual);
    }

    @Test
    void testStopGoalsByUser_Success() {
        Goal goal = Goal.builder()
                .title("Goal 1")
                .users(List.of(User.builder().id(1L).build()))
                .build();

        when(goalRepository.findGoalsByUserId(1L)).thenReturn(Stream.of(goal));

        goalService.stopGoalsByUser(1L);

        verify(goalRepository, times(1)).delete(goal);
    }
}