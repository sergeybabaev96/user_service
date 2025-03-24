package school.faang.user_service.validation.goal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalCreateDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class GoalValidatorTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GoalValidator validator;

    private GoalCreateDto goalDto;
    private Long userId;
    private Long goalId;
    private Goal goal;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        goalDto = new GoalCreateDto();
        goalDto.setTitle("test");
        userId = 1L;
        goalId = 1L;
        goal = new Goal();
    }

    @Test
    @DisplayName("Создание задачи с null названием")
    void testValidateCreatedNullTitle() {
        goalDto.setTitle(null);

        Exception exception = assertThrows(DataValidationException.class,
                () -> validator.validateCreated(userId, goalDto));

        assertEquals("Название цели пустое", exception.getMessage());
    }

    @Test
    @DisplayName("Создание задачи с пустым названием")
    void testValidateCreatedBlankTitle() {
        goalDto.setTitle("    ");

        Exception exception = assertThrows(DataValidationException.class,
                () -> validator.validateCreated(userId, goalDto));

        assertEquals("Название цели пустое", exception.getMessage());
    }

    @Test
    @DisplayName("Создание задачи для несуществующего пользователя")
    void testValidateCreatedUserNotFound() {
        Mockito.when(userRepository.findById(goalId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(DataValidationException.class,
                () -> validator.validateCreated(userId, goalDto));

        assertEquals("Пользователь не найден: " + userId, exception.getMessage());
    }

    @Test
    @DisplayName("Создание задачи для пользователя с граничным количеством активных целей")
    void testValidateCreatedBorderActiveGoals() {
        Mockito.when(userRepository.findById(goalId)).thenReturn(Optional.of(user));
        Mockito.when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(3);

        Exception exception = assertThrows(DataValidationException.class,
                () -> validator.validateCreated(userId, goalDto));

        assertEquals("Превышено максимальное количество целей: 3", exception.getMessage());
    }

    @Test
    @DisplayName("Создание задачи для пользователя с превышенным количеством активных целей")
    void testValidateCreatedExcessActiveGoals() {
        Mockito.when(userRepository.findById(goalId)).thenReturn(Optional.of(user));
        Mockito.when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(4);

        Exception exception = assertThrows(DataValidationException.class,
                () -> validator.validateCreated(userId, goalDto));

        assertEquals("Превышено максимальное количество целей: 3", exception.getMessage());
    }

    @Test
    @DisplayName("Создание задачи с несуществующими навыками")
    void testValidateCreatedSkillsNotExistAndNotNull() {
        goalDto.setSkillsToAchieveId(List.of(1L, 2L, 3L));
        List<Long> skillsId = goalDto.getSkillsToAchieveId();
        Mockito.when(userRepository.findById(goalId)).thenReturn(Optional.of(user));
        Mockito.when(skillRepository.countExisting(skillsId)).thenReturn(0);

        Exception exception = assertThrows(DataValidationException.class,
                () -> validator.validateCreated(userId, goalDto));

        assertEquals("Цель содержит несуществующие навыки", exception.getMessage());
    }

    @Test
    @DisplayName("Создание задачи с валидными входными данными")
    void testValidateCreatedSuccess() {
        Mockito.when(userRepository.findById(goalId)).thenReturn(Optional.of(user));
        Mockito.when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(2);

        assertDoesNotThrow(() -> validator.validateCreated(userId, goalDto));
    }

    @Test
    @DisplayName("Обновление несуществующей задачи")
    void validateUpdatedNonExistingGoal() {
        goalId = null;

        Exception exception = assertThrows(DataValidationException.class,
                () -> validator.validateUpdated(goalId, goalDto));

        assertEquals("Цель не найдена: " + goalId, exception.getMessage());
    }

    @Test
    @DisplayName("Обновление уже завершенной задачи")
    void validateUpdatedCompletedGoal() {
        goal.setStatus(GoalStatus.COMPLETED);
        Mockito.when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));

        Exception exception = assertThrows(DataValidationException.class,
                () -> validator.validateUpdated(goalId, goalDto));

        assertEquals("Цель уже завершена", exception.getMessage());
    }

    @Test
    @DisplayName("Обновление задачи с валидными входными данными")
    void validateUpdatedSuccess() {
        goal.setStatus(GoalStatus.ACTIVE);
        Mockito.when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));

        assertDoesNotThrow(() -> validator.validateUpdated(goalId, goalDto));
    }
}
