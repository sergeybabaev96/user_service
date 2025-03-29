package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalCreateDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.GoalViewDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityAlreadyExistException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.filter.goal.filters.TestGoalStatusFilter;
import school.faang.user_service.filter.goal.filters.TestGoalTitleFilter;
import school.faang.user_service.mapper.GoalMapperImpl;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validation.goal.GoalValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private GoalValidator goalValidator;

    @Spy
    private TestGoalStatusFilter statusFilter;

    @Spy
    private TestGoalTitleFilter titleFilter;

    @Spy
    private GoalMapperImpl goalMapper;

    private GoalService goalService;

    private final Goal goal = new Goal();
    private final GoalCreateDto goalCreateDto = new GoalCreateDto();
    private final GoalViewDto goalViewDto = new GoalViewDto();
    private final GoalFilterDto filter = new GoalFilterDto();
    private final User user = new User();
    private final Skill skill = new Skill();

    @BeforeEach
    void setUp() {
        user.setId(1L);
        user.setGoals(new ArrayList<>());
        user.setSkills(new ArrayList<>());

        goalCreateDto.setTitle("goal title");

        goal.setId(1L);
        goal.setSkillsToAchieve(new ArrayList<>());

        skill.setId(1L);

        filter.setTitle("java");
        filter.setStatus(GoalStatus.ACTIVE);

        goalService = new GoalService(
                goalRepository,
                skillRepository,
                goalValidator,
                goalMapper,
                List.of(titleFilter, statusFilter),
                userService,
                userRepository
                );
    }

    @Test
    @DisplayName("Успешное создание цели")
    void createGoalPositive() {
        Mockito.when(goalMapper.toEntity(goalCreateDto)).thenReturn(goal);
        Mockito.when(userService.getUser(user.getId())).thenReturn(user);
        Mockito.when(goalMapper.toDto(goal)).thenReturn(goalViewDto);
        Mockito.when(goalRepository.save(goal)).thenReturn(goal);

        GoalViewDto result = goalService.createGoal(user.getId(), goalCreateDto);

        assertNotNull(result);
        assertEquals(goalViewDto, result);
    }

    @Test
    @DisplayName("Создание уже существующей цели")
    void createGoalAlreadyExist() {
        Mockito.when(goalMapper.toEntity(goalCreateDto)).thenReturn(goal);
        Mockito.when(userService.getUser(user.getId())).thenReturn(user);
        user.setGoals(List.of(goal));

        Exception exception = assertThrows(EntityAlreadyExistException.class,
                () -> goalService.createGoal(user.getId(), goalCreateDto));

        assertEquals("У пользователя " + user.getId() + " уже есть цель " + goal, exception.getMessage());
    }

    @Test
    @DisplayName("Создание цели для несуществующего пользователя")
    void createGoalNonExistentUser() {
        Mockito.when(userService.getUser(user.getId())).thenThrow(new DataValidationException("Пользователь не найден"));

        Exception exception = assertThrows(DataValidationException.class,
                () -> goalService.createGoal(user.getId(), goalCreateDto));

        assertEquals("Пользователь не найден", exception.getMessage());
    }

    @Test
    @DisplayName("Успешное обновление цели")
    void updateGoalPositive() {
        Mockito.when(goalRepository.findById(goal.getId())).thenReturn(Optional.of(goal));
        Mockito.when(goalRepository.findUsersByGoalId(goal.getId())).thenReturn(List.of(user));
        Mockito.when(goalRepository.save(goal)).thenReturn(goal);

        goal.setTitle("title");
        goalCreateDto.setStatus(GoalStatus.COMPLETED);
        goalCreateDto.setDescription("goal description");
        goalService.updateGoal(goal.getId(), goalCreateDto);

        assertEquals(goal.getTitle(), goalCreateDto.getTitle());
        assertEquals(goal.getDescription(), goalCreateDto.getDescription());
        assertEquals(goal.getStatus(), goalCreateDto.getStatus());
    }

    @Test
    @DisplayName("Не добавляем пользователю новые навыки, если обновленная цель имеет активный статус")
    void updateGoalNotAddingNewSkillsToUserIfGoalIsActive() {
        Mockito.when(goalRepository.findById(goal.getId())).thenReturn(Optional.of(goal));
        goalCreateDto.setStatus(GoalStatus.ACTIVE);
        goal.getSkillsToAchieve().add(skill);

        goalService.updateGoal(goal.getId(), goalCreateDto);

        assertNotEquals(user.getSkills(), goal.getSkillsToAchieve());
    }

    @Test
    @DisplayName("Успешное удаление цели")
    void deleteGoalPositive() {
        Mockito.when(goalRepository.findById(goal.getId())).thenReturn(Optional.of(goal));
        Mockito.when(goalRepository.findUsersByGoalId(goal.getId())).thenReturn(List.of(user));
        Mockito.when(userRepository.save(user)).thenReturn(user);
        user.getGoals().add(goal);

        goalService.deleteGoal(goal.getId());

        assertFalse(user.getGoals().contains(goal));
        Mockito.verify(goalRepository, times(1)).delete(goal);
    }

    @Test
    @DisplayName("Удаление несуществующей цели")
    void deleteGoalNonExistentGoal() {
        Mockito.when(goalRepository.findById(goal.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> goalService.deleteGoal(goal.getId()));

        assertEquals("Цель " + goal.getId() + " не найдена", exception.getMessage());
    }

    @Test
    @DisplayName("Успешный поиск подцелей")
    void findSubtasksByGoalIdPositive() {
        Stream<Goal> subGoals = Stream.of(
                Goal.builder().title("AAjava__").status(GoalStatus.ACTIVE).build(),
                Goal.builder().title("python").build(),
                Goal.builder().title("java").status(GoalStatus.COMPLETED).build()
        );
        Mockito.when(goalRepository.findByParent(goal.getId())).thenReturn(subGoals);
        List<GoalViewDto> result = goalService.findSubtasksByGoalId(goal.getId(), filter);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Поиск с пустым списком подцелей")
    void findSubtasksByGoalIdNoSubGoals() {
        Stream<Goal> subGoals = Stream.of();
        Mockito.when(goalRepository.findByParent(goal.getId())).thenReturn(subGoals);
        List<GoalViewDto> result = goalService.findSubtasksByGoalId(goal.getId(), filter);

        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Поиск с пустым списком целей пользователя")
    void getGoalsByUserEmptyGoals() {
        Stream<Goal> userGoals = Stream.of();
        Mockito.when(goalRepository.findGoalsByUserId(goal.getId())).thenReturn(userGoals);
        List<GoalViewDto> result = goalService.getGoalsByUser(goal.getId(), filter);

        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Успешный поиск целей пользователя по фильтру")
    void getGoalsByUserPositive() {
        Stream<Goal> userGoals = Stream.of(
                Goal.builder().title("buy java books").status(GoalStatus.ACTIVE).build(),
                Goal.builder().title("c++").build(),
                Goal.builder().title("learn java").status(GoalStatus.ACTIVE).build()
        );
        Mockito.when(goalRepository.findGoalsByUserId(goal.getId())).thenReturn(userGoals);
        List<GoalViewDto> result = goalService.getGoalsByUser(goal.getId(), filter);

        assertEquals(2, result.size());
    }
}