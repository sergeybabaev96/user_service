package school.faang.user_service.service.goal;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.goal.GoalCreateDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.dto.goal.GoalViewDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.EntityAlreadyExistException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validation.goal.GoalValidator;

import java.util.List;
import java.util.stream.Stream;

/**
 * Сервис для управления целями.
 * <p>
 * Этот сервис предоставляет методы для создания, обновления, удаления и получения целей.
 * </p>
 * <p>
 * Основные функции:
 * <ul>
 *     <li>{@link #createGoal(Long, GoalCreateDto) Создание новой цели} с проверкой валидности данных.</li>
 *     <li>{@link #updateGoal(Long, GoalCreateDto) Обновление существующей цели} с проверкой валидности данных.</li>
 *     <li>{@link #deleteGoal(Long) Удаление цели} по её идентификатору.</li>
 *     <li>{@link #findSubtasksByGoalId(Long, GoalFilterDto) Получение списка подцелей по родительской цели с применением фильтра}.</li>
 *     <li>{@link #getGoalsByUser(Long, GoalFilterDto) Получение списка всех целей пользователя с применением фильтра}.</li>
 * </ul>
 * </p>
 * @author juzu400
 * @see GoalCreateDto
 * @see GoalViewDto
 * @see GoalFilterDto
 * @see UserService
 * @see GoalFilter
 * @see Goal
 * @see User
 * @see Skill
 * @see GoalValidator
 */
@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final SkillRepository skillRepository;
    private final GoalValidator goalValidator;
    private final GoalMapper goalMapper;
    private final List<GoalFilter> goalFilter;
    private final UserService userService;
    private final UserRepository userRepository;

    /**
     * Создание новой цели
     *
     * @param userId Идентификатор пользователя, для которого создаётся цель
     * @param goal DTO с данными цели для её создания
     * @return Созданная цель
     */
    public GoalViewDto createGoal(@NotNull Long userId, @NotNull GoalCreateDto goal) {
        goalValidator.validateCreated(userId, goal);
        Goal goalEntity = goalMapper.toEntity(goal);
        userAddGoal(userId, goalEntity);
        goalRepository.save(goalEntity);
        return goalMapper.toDto(goalEntity);
    }

    /**
     * Обновление существующей цели
     *
     * @param goalId Идентификатор цели, которую необходимо обновить
     * @param goalDto DTO с новыми данными цели
     * @return Обновлённая цель
     */
    public GoalViewDto updateGoal(@NotNull Long goalId, @NotNull GoalCreateDto goalDto) {
        goalValidator.validateUpdated(goalId, goalDto);
        Goal goal = findGoalById(goalId);
        isGoalCompleted(goal, goalDto);
        goalMapper.update(goalDto, goal);
        goalRepository.save(goal);
        return goalMapper.toDto(goal);
    }

    /**
     * Удаление цели
     *
     * @param goalId Идентификатор цели, которую необходимо удалить
     */
    public void deleteGoal(@NotNull Long goalId) {
        Goal goal = findGoalById(goalId);
        List<User> users = goalRepository.findUsersByGoalId(goalId);
        users.forEach(user -> {
            user.getGoals().remove(goal);
            userRepository.save(user);
        });
        goalRepository.delete(goal);
    }

    /**
     * Получение подцелей по родительской цели с применением фильтра
     *
     * @param goalId Идентификатор родительской цели
     * @param filter DTO с данными для фильтрации
     * @return Список подцелей по фильтру
     */
    public List<GoalViewDto> findSubtasksByGoalId(@NotNull Long goalId, @NotNull GoalFilterDto filter) {
        var goals = goalRepository.findByParent(goalId);
        return applyFilters(goals, filter);
    }

    /**
     * Получение списка целей пользователя с применением фильтра
     *
     * @param userId Идентификатор пользователя
     * @param filter DTO с данными для фильтрации
     * @return Список целей пользователя по фильтру
     */
    public List<GoalViewDto> getGoalsByUser(@NotNull Long userId, @NotNull GoalFilterDto filter) {
        var goals = goalRepository.findGoalsByUserId(userId);
        return applyFilters(goals, filter);
    }

    /**
     * Добавление цели пользователю
     *
     * @param userId Идентификатор пользователя
     * @param goal Цель
     * @throws EntityAlreadyExistException Если у пользователя уже есть данная цель
     */
    private void userAddGoal(Long userId, Goal goal) {
        User user = userService.getUser(userId);
        if (!user.getGoals().contains(goal)) {
            user.getGoals().add(goal);
            userRepository.save(user);
        } else {
            throw new EntityAlreadyExistException("У пользователя " + userId + " уже есть цель " + goal);
        }
    }

    /**
     * Применение фильтров
     *
     * @param goals Поток целей, для которых применяется фильтрация
     * @param filter DTO c данными для фильтрации
     * @return Список отфильтрованных целей
     */
    private List<GoalViewDto> applyFilters(Stream<Goal> goals, GoalFilterDto filter) {
        for (GoalFilter goalFilter : goalFilter) {
            if (goalFilter.isApplicable(filter)) {
                goals = goalFilter.apply(goals, filter);
            }
        }
        return goals
                .map(goalMapper::toDto)
                .toList();
    }

    /**
     * Поиск цели по идентификатору
     *
     * @param goalId Идентификатор цели
     * @return Найденная цель
     * @throws EntityNotFoundException Если данная цель не существует
     */
    private Goal findGoalById(Long goalId) {
        return goalRepository.findById(goalId)
                .orElseThrow(() -> new EntityNotFoundException("Цель " + goalId + " не найдена"));
    }

    /**
     * Проверка, что цель стала завершённой
     *
     * @param goal Цель
     * @param goalDto DTO с новыми данными для обновления цели
     */
    private void isGoalCompleted(Goal goal, GoalCreateDto goalDto) {
        if (goalDto.getStatus() != GoalStatus.COMPLETED) {
            return;
        }
        List<Skill> skills = goal.getSkillsToAchieve();
        assignSkillsToUsers(goalRepository.findUsersByGoalId(goal.getId()), skills);
    }

    /**
     * Добавление пользователю навыков
     *
     * @param users Список пользователей
     * @param skills Список навыков
     */
    private void assignSkillsToUsers(List<User> users, List<Skill> skills) {
        users.forEach(user -> {
            skills.stream()
                    .filter(skill -> !user.getSkills().contains(skill))
                    .forEach(skill -> skillRepository.assignSkillToUser(skill.getId(), user.getId()));
            userRepository.save(user);
        });
    }
}
