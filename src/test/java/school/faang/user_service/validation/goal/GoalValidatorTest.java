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
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class GoalValidatorTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private GoalValidator validator;

    private GoalCreateDto goalDto;
    private Long userId;
    private Long goalId;
    private Goal goal;

    @BeforeEach
    void setUp() {
        goalDto = new GoalCreateDto();
        goalDto.setTitle("test");
        userId = 1L;
        goalId = 1L;
        goal = new Goal();
    }

    @Test
    @DisplayName("Создание задачи с null названием")
    void testValidateCreationNullTitle() {
        goalDto.setTitle(null);
        assertThrows(DataValidationException.class, () -> validator.validateCreation(userId, goalDto));
    }

    @Test
    @DisplayName("Создание задачи с пустым названием")
    void testValidateCreationBlankTitle() {
        goalDto.setTitle("    ");
        assertThrows(DataValidationException.class, () -> validator.validateCreation(userId, goalDto));
    }

    @Test
    @DisplayName("Создание задачи для пользователя с граничным количеством активных целей")
    void testValidateCreationBorderActiveGoals() {
        Mockito.when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(3);

        assertThrows(DataValidationException.class, () -> validator.validateCreation(userId, goalDto));
    }

    @Test
    @DisplayName("Создание задачи для пользователя с превышенным количеством активных целей")
    void testValidateCreationExcessActiveGoals() {
        Mockito.when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(4);

        assertThrows(DataValidationException.class, () -> validator.validateCreation(userId, goalDto));
    }

    @Test
    @DisplayName("Создание задачи с несуществующими навыками")
    void testValidateCreationSkillsNotExistAndNotNull() {
        goalDto.setSkillsToAchieveId(List.of(1L, 2L, 3L));
        List<Long> skillsId = goalDto.getSkillsToAchieveId();
        Mockito.when(skillRepository.countExisting(skillsId)).thenReturn(0);

        assertThrows(DataValidationException.class, () -> validator.validateCreation(userId, goalDto));
    }

    @Test
    @DisplayName("Создание задачи с валидными входными данными")
    void testValidateCreationSuccess() {
        Mockito.when(goalRepository.countActiveGoalsPerUser(userId)).thenReturn(2);

        assertDoesNotThrow(() -> validator.validateCreation(userId, goalDto));
    }

    @Test
    @DisplayName("Обновление несуществующей задачи")
    void validateUpdateNonExistingGoal() {
        goalId = null;

        assertThrows(EntityNotFoundException.class, () -> validator.validateUpdate(goalId, goalDto));
    }

    @Test
    @DisplayName("Обновление уже завершенной задачи")
    void validateUpdateCompletedGoal() {
        goal.setStatus(GoalStatus.COMPLETED);
        Mockito.when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));

        assertThrows(DataValidationException.class, () -> validator.validateUpdate(goalId, goalDto));
    }

    @Test
    @DisplayName("Обновление задачи с валидными входными данными")
    void validateUpdateSuccess() {
        goal.setStatus(GoalStatus.ACTIVE);
        Mockito.when(goalRepository.findById(goalId)).thenReturn(Optional.of(goal));

        assertDoesNotThrow(() -> validator.validateUpdate(goalId, goalDto));
    }
}
